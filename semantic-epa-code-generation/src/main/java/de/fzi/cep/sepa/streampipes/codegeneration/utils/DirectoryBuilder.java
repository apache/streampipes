package de.fzi.cep.sepa.streampipes.codegeneration.utils;

import java.io.File;

public abstract class DirectoryBuilder {
	
	public static boolean createDirectories(String[] dirs) {
		
		for (String dir : dirs) {
			if (!createDirectory(dir)) {
				return false;
			}
		}

		return true;
	}

	private static boolean createDirectory(String dir) {
		dir = dir.replaceAll("/", File.separator);
		if (dir != null) {
			return (new File(dir)).mkdirs();
		} else {
			return false;
		}
	}

}
