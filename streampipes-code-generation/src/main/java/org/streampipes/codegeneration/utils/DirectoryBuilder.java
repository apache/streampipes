package org.streampipes.codegeneration.utils;

import java.io.File;

public abstract class DirectoryBuilder {
	
	/**
	 * Use the / in the routes. It will be automatically replaced with File.seperator
	 * @param dirs an array with directories that are created
	 * @return whether the directory structure was created successfully 
	 */
	public static boolean createDirectories(String[] dirs) {
		
		for (String dir : dirs) {
			if (!createDirectory(dir)) {
				return false;
			}
		}

		return true;
	}

	private static boolean createDirectory(String dir) {
		String escapedSeparator = File.separator.equals("\\") ? "\\\\" : File.separator;
		dir = dir.replaceAll("/", escapedSeparator);
		if (dir != null) {
			return (new File(dir)).mkdirs();
		} else {
			return false;
		}
	}

}
