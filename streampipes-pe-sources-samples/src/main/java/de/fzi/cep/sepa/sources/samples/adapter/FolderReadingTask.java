package de.fzi.cep.sepa.sources.samples.adapter;


public class FolderReadingTask {

	private String directory;
	private String filenamePrefix;
	private String fileType;
	
	private int startIndex;
	private int endIndex;
		
	
	public FolderReadingTask(String directory, String filenamePrefix, String fileType,
			int startIndex, int endIndex) {
		super();
		this.directory = directory;
		this.filenamePrefix = filenamePrefix;
		this.fileType = fileType;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}
	
	public String getDirectory() {
		return directory;
	}
	public void setDirectory(String directory) {
		this.directory = directory;
	}
	public String getFilenamePrefix() {
		return filenamePrefix;
	}
	public void setFilenamePrefix(String filenamePrefix) {
		this.filenamePrefix = filenamePrefix;
	}
	public int getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	public int getEndIndex() {
		return endIndex;
	}
	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
		
}
