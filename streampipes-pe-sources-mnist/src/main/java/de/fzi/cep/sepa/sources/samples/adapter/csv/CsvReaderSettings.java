package de.fzi.cep.sepa.sources.samples.adapter.csv;


import java.io.File;
import java.util.List;

public class CsvReaderSettings {

	private List<File> csvInputFiles;

	private String columnSeparator;
	private Integer timestampColumnName;
	
	private boolean headerIncluded;
//	private ThriftLineParser lineParser;
	
	public CsvReaderSettings(List<File> csvInputFiles,
							 String columnSeparator,
							 Integer timestampColumnName,
//							 ThriftLineParser lineParser,
							 boolean headerIncluded) {
		super();
		this.csvInputFiles = csvInputFiles;
		this.columnSeparator = columnSeparator;
		this.timestampColumnName = timestampColumnName;
		this.headerIncluded = headerIncluded;
//		this.lineParser = lineParser;
	}

	public List<File> getCsvInputFiles() {
		return csvInputFiles;
	}

	public void setCsvInputFiles(List<File> csvInputFiles) {
		this.csvInputFiles = csvInputFiles;
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

//	public ThriftLineParser getLineParser() {
//		return lineParser;
//	}

//	public void setLineParser(ThriftLineParser lineParser) {
//		this.lineParser = lineParser;
//	}

	public Integer getTimestampColumnName() {
		return timestampColumnName;
	}

	public void setTimestampColumnName(Integer timestampColumnName) {
		this.timestampColumnName = timestampColumnName;
	}
				
}
