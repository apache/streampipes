package org.streampipes.pe.sources.samples.hella;

import org.streampipes.messaging.EventProducer;
import org.streampipes.messaging.kafka.SpKafkaProducer;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.pe.sources.samples.adapter.CsvPublisher;
import org.streampipes.pe.sources.samples.adapter.CsvReadingTask;
import org.streampipes.pe.sources.samples.adapter.FolderReadingTask;
import org.streampipes.pe.sources.samples.adapter.LineParser;
import org.streampipes.pe.sources.samples.adapter.SimulationSettings;
import org.streampipes.pe.sources.samples.config.SourcesConfig;
import org.streampipes.pe.sources.samples.hella.parser.MaterialMovementParser;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.vocabulary.SO;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MaterialMovementStream extends AbstractHellaStream {

	public static final String montracDataFolder =System.getProperty("user.home") + File.separator +".streampipes" +File.separator +"sources" +File.separator +"data" +File.separator +"montrac" +File.separator;

	private static final List<String> fileNamePrefixes = Arrays.asList("20150910-", "20150911-", "20150912-", "20150913-", "20150914-", "20150915-", "20150916-");

	
	@Override
	public SpDataStream declareModel(DataSourceDescription sep) {
		
		SpDataStream stream = prepareStream(HellaVariables.MontracMovement.topic());
		
		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(EpProperties.stringEp(Labels.empty(), "variable_type", SO.Text));
		eventProperties.add(EpProperties.longEp(Labels.empty(), "variable_timestamp", "http://schema.org/DateTime"));
		eventProperties.add(EpProperties.stringEp(Labels.empty(), "location", Arrays.asList(URI.create("http://hella.de/hella#montracLocationId"), URI.create(SO.Text))));
		eventProperties.add(EpProperties.stringEp(Labels.empty(), "event", Arrays.asList(URI.create("http://hella.de/hella#montracEvent"), URI.create(SO.Text))));
		eventProperties.add(EpProperties.integerEp(Labels.empty(), "shuttle", Arrays.asList(URI.create("http://hella.de/hella#shuttleId"), URI.create(SO.Number))));
		eventProperties.add(EpProperties.booleanEp(Labels.empty(), "rightPiece", "http://schema.org/Boolean"));
		eventProperties.add(EpProperties.stringEp(Labels.empty(), "leftPiece", "http://schema.org/Boolean"));
		
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setName(HellaVariables.MontracMovement.eventName());
		stream.setDescription(HellaVariables.MontracMovement.description());
		stream.setUri(sep.getUri() + "/montrac");
		
		return stream;
	}

	@Override
	public void executeStream() {
		
		System.out.println("Execute Montrac");
		EventProducer publisher = new SpKafkaProducer(SourcesConfig.INSTANCE.getKafkaUrl(), HellaVariables
            .MontracMovement.topic());
		
		//IMessagePublisher publisher = new ConsoleLoggingPublisher();
		
		LineParser montracLineParser = new MaterialMovementParser();
		CsvReadingTask csvReadingTask = new CsvReadingTask(makeFolderReadingTasks(), ",", "variable_timestamp", montracLineParser, true);
				
		Thread mouldingReplayThread = new Thread(new CsvPublisher(publisher, csvReadingTask, SimulationSettings.DEMONSTRATE_10));
		mouldingReplayThread.start();
	}

	private List<FolderReadingTask> makeFolderReadingTasks() {
		List<FolderReadingTask> tasks = new ArrayList<>();
		
		for(String fileNamePrefix : fileNamePrefixes)
		{
			FolderReadingTask task = new FolderReadingTask(montracDataFolder, fileNamePrefix, "csv", 0, 0);
			tasks.add(task);
		}
		return tasks;
	}

	@Override
	public boolean isExecutable() {
		return true;
	}

}
