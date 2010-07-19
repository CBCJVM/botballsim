/*
 * This file is part of JBSim.
 * 
 * JBSim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * JBSim is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with JBSim.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.icx.sim;

import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * Re-parses user C/IC code into Java code for simulator.
 * 
 * @author Stephen Carlson
 */
public class CodeParser {
	// Removes C function prototypes in generated file
	public static final Pattern PROTOTYPE = Pattern.compile("([a-z][a-z_0-9]*) [a-z][a-z_0-9]* \\(.*\\)\\s*?;",
		Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
	// Trigger Java include hot comments in generated file
	public static final Pattern JIT = Pattern.compile("//\\s*?#");

	/**
	 * Reads data from the given reader and writes to Program.java for running.
	 * 
	 * @param source the path where the code file was obtained. If null, #include and #use
	 *  will die with an exception if encountered.
	 * @param r the code data source
	 * @throws Exception if something goes wrong:
	 * - I/O
	 * - out of memory (really big file?)
	 * - some types of grievous syntax errors
	 * - binary file
	 * - ...
	 */
	public static void syntax(File source, Reader r) throws Exception {
		List<Token> ll = parse(source, r);
		macroExpand(ll);
		writeData(doFinal(ll), "Program.java");
	}

	// Condenses tokens down into text to be written.
	//  Inserts lots of unnecessary spaces which are ignored.
	private static String doFinal(List<Token> ll) throws Exception {
		StringBuilder theFinal = new StringBuilder(32768);
		theFinal.append("public class Program extends org.icx.sim.BotballProgram {\n");
		// fix spacing?
		for (Token t : ll)
			theFinal.append(t.toString() + " ");
		theFinal.append("\n}");
		return theFinal.toString();
	}

	// Writes data to the file, applying the last filter directives.
	private static void writeData(String data, String name) throws Exception {
		StringTokenizer content = new StringTokenizer(data, "\n");
		String line;
		BufferedWriter pr = new BufferedWriter(new FileWriter(name));
		while (content.hasMoreTokens()) {
			line = content.nextToken();
			// strip function prototypes (only detectable here?)
			Matcher m = PROTOTYPE.matcher(line);
			if (m.find() && !m.group(1).equals("return"))
				line = m.replaceFirst("");
			// java inline includes
			line = JIT.matcher(line).replaceAll("");
			pr.write(line);
			pr.write('\n');
		}
		pr.close();
	}

	// Breaks down input into tokens
	private static List<Token> parse(File file, Reader r) throws Exception {
		// handles comments and strings for us
		StreamTokenizer str = new StreamTokenizer(r);
		str.resetSyntax();
		str.eolIsSignificant(true);
		str.whitespaceChars(0, ' ');
		str.wordChars(' ' + 1, 255);
		str.ordinaryChar(')');
		str.ordinaryChar(']');
		str.ordinaryChar('[');
		str.ordinaryChar('(');
		str.ordinaryChar(',');
		str.ordinaryChar(';');
		str.quoteChar('"');
		int type = 0;
		boolean pound = false, include = false; String intern = null;
		List<Token> ll = new LinkedList<Token>();
		// iterate through tokens
		while (true) {
			type = str.nextToken();
			if (type == StreamTokenizer.TT_EOF) break;
			switch (type) {
			case StreamTokenizer.TT_WORD:
				// pre processor
				if (str.sval.startsWith("#") && !pound) {
					pound = true;
					intern = "";
					include = str.sval.equals("#include") || str.sval.equals("#use");
					if (!include)
						ll.add(new Token(StreamTokenizer.TT_WORD, str.sval, '#'));
				} else if (pound)
					// pre processor value
					intern += str.sval + " ";
				else
					ll.add(new Token(StreamTokenizer.TT_WORD, str.sval, (char)type));
				break;
			case StreamTokenizer.TT_EOL:
				if (!pound)
					ll.add(new Token(StreamTokenizer.TT_EOL, "\n", '\n'));
				else {
					// close pre processor
					if (include) {
						// find and insert the specified file here
						if (file == null)
							throw new Exception("Cannot use #include or #use here.");
						String path = intern.trim();
						if (path.length() < 2)
							throw new Exception("No file given to #include.");
						if (path.startsWith("\"") && path.endsWith("\""))
							path = path.substring(1, path.length() - 1);
						File toInc = new File(file.getAbsoluteFile().getParentFile(), path);
						try {
							FileReader r2 = new FileReader(toInc);
							ll.addAll(parse(toInc, r2));
							r2.close();
						} catch (IOException e) {
							throw new Exception("Cannot include " + path + ": File not found.", e);
						}
					} else
						ll.add(new Token(StreamTokenizer.TT_WORD, intern.trim(), '\u0000'));
					include = pound = false;
					intern = null;
					ll.add(new Token(StreamTokenizer.TT_EOL, "\n", '\n'));
				}
				break;
			default:
				// just add item to the list
				if (pound) {
					if (type == '"' || type == '\'')
						intern += (char)type + str.sval + (char)type + " ";
					else
						intern += (char)type + " ";
				} else
					ll.add(new Token(type, str.sval, (char)type));
			}
		}
		return ll;
	}

	// Utility method to fetch token based off of a word
	private static Token singleToken(String word) {
		return new Token(StreamTokenizer.TT_WORD, word, '\u0000');
	}

	// Expands macros in the code.
	//  TODO support for arguments, i.e. #define f(x) -2*x*x + 3*x - 5
	private static void macroExpand(List<Token> ll) throws Exception {
		Map<String, List<Token>> macro = new HashMap<String, List<Token>>(32);
		// type conversions
		macro.put("long double", listOf(singleToken("double")));
		macro.put("float", listOf(singleToken("double")));
		macro.put("bool", listOf(singleToken("boolean")));
		macro.put("long long", listOf(singleToken("long")));
		macro.put("long float", listOf(singleToken("double")));
		macro.put("long int", listOf(singleToken("long")));
		macro.put("short", listOf(singleToken("int")));
		macro.put("persistent", listOf(singleToken("static")));
		//macro.put("struct", listOf(singleToken("class")));
		macro.put("bool", listOf(singleToken("boolean")));
		macro.put("NULL", listOf(singleToken("null")));
		ListIterator<Token> it = ll.listIterator();
		Token t; String s; int index;
		// iterate through all tokens
		while (it.hasNext()) {
			t = it.next();
			if (t.type == StreamTokenizer.TT_WORD && t.cval == '#')
				if (it.hasNext()) {
					s = t.sval;
					t = it.next();
					if (t.type == StreamTokenizer.TT_WORD) {
						index = t.sval.indexOf(' ');
						if (s.equals("#define") && index > 0) {
							// resolve macro and add to the table
							s = t.sval;
							macro.put(s.substring(0, index).trim(),
								parse(null, new StringReader(s.substring(index + 1))));
							it.remove();
							// clean off junked directive
							it.previous();
							it.remove();
						} else
							throw new Exception("Not a supported preprocessor type.");
					} else
						throw new Exception("Invalid preprocessor argument.");
				} else
					throw new Exception("Preprocessor missing argument.");
		}
		// rewind and replace
		it = ll.listIterator();
		List<Token> ll2;
		Iterator<Token> it2;
		while (it.hasNext()) {
			t = it.next();
			if (t.type == StreamTokenizer.TT_WORD) {
				ll2 = macro.get(t.sval);
				if (ll2 != null) {
					// replace with macro text
					it.remove();
					it2 = ll2.iterator();
					while (it2.hasNext())
						it.add(it2.next());
				}
			}
		}
		// a few odd expansions
		String twoAgo = null, oneAgo = null;
		it = ll.listIterator();
		while (it.hasNext()) {
			t = it.next();
			if (t.cval == '[') {
				// fix arrays to Java style
				Token t2 = it.next();
				if (t2.cval != ']' && twoAgo != null && (twoAgo.equals("long") ||
						twoAgo.equals("double") || twoAgo.equals("int") || twoAgo.equals("char"))) {
					it.remove();
					it.next();
					// change int x[16] to int[] x = new int[16]
					it.add(new Token('=', "=", '='));
					it.add(singleToken("new " + twoAgo));
					it.add(new Token('[', "[", '['));
					it.add(t2);
					it.add(new Token(']', "]", ']'));
				}
			} else if (t.type == StreamTokenizer.TT_WORD && twoAgo != null &&
					(twoAgo.equals("start_process") || twoAgo.equals("run_for"))) {
				// rudimentary function pointer fix
				it.remove();
				it.add(singleToken("\"" + t.sval + "\""));
			} else if (t.cval == '(' && oneAgo != null && (oneAgo.equals("if") || oneAgo.equals("while")));
				/*
				 * TODO if statements are broken:
				 * 
				 * if (true) is OK, but if (1) is not
				 * probably should insert if (test(1)) which would work
				 * but need to resolve the other parenthesis
				 * 
				 * it.add(singleToken("test("));
				 */
			if (oneAgo != null) twoAgo = oneAgo.trim();
			oneAgo = t.sval;
		}
	}

	/**
	 * Creates and returns a list of the items given.
	 * 
	 * @param args the items to list
	 * @return a list of those items
	 */
	private static <T> List<T> listOf(T... args) {
		List<T> list = new ArrayList<T>(args.length);
		for (int i = 0; i < args.length; i++)
			list.add(args[i]);
		return list;
	}

	/**
	 * A class which represents a token parsed from user code.
	 */
	private static class Token {
		// Type as defined by stream tokenizer: word, character, line break...
		public int type;
		// value as a String
		public String sval;
		// value as character
		public char cval;

		// Creates a token with the given values
		public Token(int type, String sval, char cval) {
			this.type = type;
			this.sval = sval;
			this.cval = cval;
			if (cval == '"')
				// auto escape string
				this.sval = sval.replace("\n", "\\n").replace("\r", "").replace("\t", "\\t").
					replace("\b", "\\b");
		}
		// Converts token to string for output
		public String toString() {
			if (cval == '"')
				return "\"" + sval + "\"";
			if (type == StreamTokenizer.TT_WORD)
				return sval;
			return Character.toString(cval);
		}
	}
}