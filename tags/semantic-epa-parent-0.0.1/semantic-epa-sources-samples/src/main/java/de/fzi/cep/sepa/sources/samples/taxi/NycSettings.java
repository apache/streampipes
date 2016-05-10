package de.fzi.cep.sepa.sources.samples.taxi;

import java.io.File;

public class NycSettings {
	
	public static final String pathToDataset =System.getProperty("user.home") + File.separator +".streampipes" +File.separator +"sources" +File.separator +"data" +File.separator +"taxi" +File.separator;

	public static final String completeDatasetFilename = pathToDataset +"sorted_data.csv";
	public static final String test01DatasetFilename = pathToDataset + "test_01.csv";
	public static final String test02DatasetFilename = pathToDataset + "test_02.csv";
	public static final String test03DatasetFilename = pathToDataset + "test_03.csv";
		
	public static final String sampleTopic = "SEPA.SEP.NYC.Taxi";
	public static final String test01Topic = "SEPA.SEP.NYC.Test01";
	public static final String test02Topic = "SEPA.SEP.NYC.Test02";
	public static final String test03Topic = "SEPA.SEP.NYC.Test03";
}
