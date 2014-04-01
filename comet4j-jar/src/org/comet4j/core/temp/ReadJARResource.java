/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.core.temp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ReadJARResource {

	public static void main(String[] args) throws IOException {
		String jarName = "C://VODOSSClient.jar";
		String fileName = "client.properties";
		JarFile jarFile = new JarFile(jarName);// 读入jar文件

		JarEntry entry = jarFile.getJarEntry(fileName);
		InputStream input = jarFile.getInputStream(entry);// 读入需要的文件

		readFile(input);

		jarFile.close();

	}

	private static void readFile(InputStream input)

	throws IOException {

		InputStreamReader isr =

		new InputStreamReader(input);

		BufferedReader reader = new BufferedReader(isr);

		String line;

		while ((line = reader.readLine()) != null) {

			System.out.println(line);

		}

		reader.close();

	}
}
