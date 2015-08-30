package de.fzi.cep.sepa.manager.generation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFileGenerator {

	private List<String> fileList;
	
	private File inputDirectory;
	private File outputFile;
	
	public ZipFileGenerator(File inputDirectory, File outputFile) {
		this.fileList = new ArrayList<String>();
		this.inputDirectory = inputDirectory;
		this.outputFile = outputFile;
		generateFileList(inputDirectory);
	}

	public void makeZip() {
		byte[] buffer = new byte[1024];
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		try {
			fos = new FileOutputStream(outputFile);
			zos = new ZipOutputStream(fos);

			FileInputStream in = null;

			for (String file : this.fileList) {
				ZipEntry ze = new ZipEntry(file);
				zos.putNextEntry(ze);
				try {
					in = new FileInputStream(inputDirectory + File.separator +file);
					int len;
					while ((len = in.read(buffer)) > 0) {
						zos.write(buffer, 0, len);
					}
				} finally {
					in.close();
				}
			}
			zos.closeEntry();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				zos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void generateFileList(File node) {

		if (node.isFile()) {
			fileList.add(generateZipEntry(node.toString()));

		}

		if (node.isDirectory()) {
			String[] subNote = node.list();
			for (String filename : subNote) {
				generateFileList(new File(node, filename));
			}
		}
	}
	
	private String generateZipEntry(String file)
	{
	   return file.substring(inputDirectory.toString().length() + 1, file.length());
	}
	
}
