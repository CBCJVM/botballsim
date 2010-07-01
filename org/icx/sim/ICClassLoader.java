package org.icx.sim;

import java.io.*;

/**
 * A class which loads the re-parsed user IC/C program.
 * 
 * @author Stephen Carlson
 */
public class ICClassLoader extends ClassLoader {
	// Temporary byte buffer for loading
	private ByteArrayOutputStream os = new ByteArrayOutputStream(65536);
	// Temporary byte buffer for copying
	private byte[] buffer = new byte[1024];

	// Finds the named class, see documentation for findClass()
	protected synchronized Class<?> findClass(String name) throws ClassNotFoundException {
		if (name.equals("Program")) {
			try {
				// read from Program.class
				FileInputStream r = new FileInputStream("Program.class");
				os.reset();
				copyStream(r, os);
				r.close();
				// define using byte array
				Class<?> clazz = defineClass("Program", os.toByteArray(), 0, os.size());
				resolveClass(clazz);
				return clazz;
			} catch (Exception e) {
				throw new ClassNotFoundException("IC program", e);
			}
		} else throw new ClassNotFoundException(name);
	}
	// Copies one stream to another.
	private void copyStream(InputStream is, OutputStream os) throws IOException {		
		int i;
		while (true) {
			// read from the input...
			i = is.read(buffer, 0, buffer.length);
			if (i < 0) break;
			// and write it right back to the output
			os.write(buffer, 0, i);
			os.flush();
		}
	}
}