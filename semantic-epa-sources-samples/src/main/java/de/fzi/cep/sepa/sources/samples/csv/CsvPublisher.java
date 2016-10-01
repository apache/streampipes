package de.fzi.cep.sepa.sources.samples.csv;

import java.io.BufferedReader;
import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import de.fzi.cep.sepa.commons.messaging.IMessagePublisher;
import de.fzi.cep.sepa.messaging.EventProducer;
import de.fzi.cep.sepa.sources.samples.util.Utils;

public class CsvPublisher implements Runnable {
	
	private static final Logger LOG = Logger.getAnonymousLogger();
	
	private EventProducer publisher;
	private CsvReadingTask csvSettings;
	private SimulationSettings simulationSettings;
	
	public CsvPublisher(EventProducer publisher, CsvReadingTask csvSettings, SimulationSettings simulationSettings)
	{
		this.publisher = publisher;
		this.csvSettings = csvSettings;
		this.simulationSettings = simulationSettings;
	}

	@Override
	public void run() {
		
		long previousTime = 0;
		
		for(FolderReadingTask folderTask : csvSettings.getFolderReadingTasks())
		{
			for(int i = folderTask.getStartIndex(); i <= folderTask.getEndIndex(); i++)
			{
				Optional<BufferedReader> readerOpt = Utils.getReader(buildFile(folderTask.getDirectory(), folderTask.getFilenamePrefix(), i, folderTask.getFileType()));
				if (readerOpt.isPresent())
				{
					try {
						BufferedReader br = readerOpt.get();			
						String line;
						long counter = 0;
						
						while ((line = br.readLine()) != null) {
							line = line.replaceAll("; ", ";"); 
							line = line.replaceAll(", ", ",");
							if ((counter == 0) && csvSettings.isHeaderIncluded()) 
								{
									counter++;
									continue;
								}
							Map<String, Object> message = csvSettings.getLineParser().parseLine(line.split(csvSettings.getColumnSeparator()));
							
							long currentTime = (long) message.get(csvSettings.getTimestampColumnName());
							
							if (simulationSettings.isSimulateRealOccurrenceTime())
							{
								if (previousTime == 0) previousTime = currentTime;
								long diff = currentTime - previousTime;		
								if (diff > 0) 
								{
									//LOG.info("Waiting " +diff/1000 + " seconds");
									Thread.sleep(diff/simulationSettings.getSpeedupFactor());
								}
								previousTime = currentTime;
							}	
							System.out.println(toJson(message));
							publisher.publish(toJson(message).getBytes());
						
							counter++;
							if (counter % 1000 == 0) LOG.info(counter + " Events sent.");
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private File buildFile(String folderName, String filenamePrefix, int pointer, String fileType)
	{
		File file = new File(folderName +File.separator +filenamePrefix +pointer +"." +fileType);
		return file;
	}
	
	private String toJson(Map<String, Object> rowContent)
	{
		StringBuilder json = new StringBuilder();
		json.append("{");
		
		for(String key : rowContent.keySet())
		{
			json.append(parse(key, rowContent.get(key)));	
		}
		json.setLength(json.length() - 1);
		json.append("}");
		return json.toString();
	}
	
	private String parse(final String key, Object value)
	{
		if (value.getClass().getName().equals("java.lang.String")) return Utils.toJsonString(key, value);
		else if (value.getClass().getName().equals("java.lang.Boolean")) return Utils.toJsonBoolean(key, value);
		else return Utils.toJsonNumber(key, value);
	}
	
	
}
