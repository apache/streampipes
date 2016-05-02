package de.fzi.cep.sepa.streampipes.codegeneration.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.squareup.javapoet.JavaFile;

public class Utils {

	public static String readResourceFile(String fileName) {
		ClassLoader classLoader = Utils.class.getClassLoader();
		String normalizedRoute = classLoader.getResource(fileName).getFile();

		return readFile(normalizedRoute);
	}

	public static String readFile(String fileName) {
		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(fileName));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			return sb.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return "Couldn't read file: " + fileName;
	}

	public static void writeToFile(String content, String file) {
		Path path = Paths.get(file);
		byte[] b = content.getBytes();
		try {
			Files.write(path, b);
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public static void writeToFile(JavaFile content, String location) {
		try {
			content.writeTo(new File(location));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error: Could not write to file: " + location);
		}
	}

}
