package de.fzi.cep.sepa.sources.samples.adapter;

import java.util.List;

public class CsvReadingTask {

	private List<FolderReadingTask> folderReadingTasks;	
	
	private String columnSeparator;
	private String timestampColumnName;
	
	private boolean headerIncluded;
	private LineParser lineParser;
	
	public CsvReadingTask(List<FolderReadingTask> folderReadingTasks,
			String columnSeparator, String timestampColumnName, LineParser lineParser, boolean headerIncluded) {
		super();
		this.folderReadingTasks = folderReadingTasks;
		this.columnSeparator = columnSeparator;
		this.timestampColumnName = timestampColumnName;
		this.headerIncluded = headerIncluded;
		this.lineParser = lineParser;
	}

	public List<FolderReadingTask> getFolderReadingTasks() {
		return folderReadingTasks;
	}

	public void setFolderReadingTasks(List<FolderReadingTask> folderReadingTasks) {
		this.folderReadingTasks = folderReadingTasks;
	}

	public String getColumnSeparator() {
		return columnSeparator;
	}

	public void setColumnSeparator(String columnSeparator) {
		this.columnSeparator = columnSeparator;
	}

	public boolean isHeaderIncluded() {
		return headerIncluded;
	}

	public void setHeaderIncluded(boolean headerIncluded) {
		this.headerIncluded = headerIncluded;
	}

	public LineParser getLineParser() {
		return lineParser;
	}

	public void setLineParser(LineParser lineParser) {
		this.lineParser = lineParser;
	}

	public String getTimestampColumnName() {
		return timestampColumnName;
	}

	public void setTimestampColumnName(String timestampColumnName) {
		this.timestampColumnName = timestampColumnName;
	}
				
}
