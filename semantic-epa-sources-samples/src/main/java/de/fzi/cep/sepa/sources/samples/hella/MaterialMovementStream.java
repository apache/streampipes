package de.fzi.cep.sepa.sources.samples.hella;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.commons.messaging.IMessagePublisher;
import de.fzi.cep.sepa.commons.messaging.ProaSenseInternalProducer;
import de.fzi.cep.sepa.model.builder.EpProperties;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.sources.samples.csv.CsvPublisher;
import de.fzi.cep.sepa.sources.samples.csv.CsvReadingTask;
import de.fzi.cep.sepa.sources.samples.csv.FolderReadingTask;
import de.fzi.cep.sepa.sources.samples.csv.LineParser;
import de.fzi.cep.sepa.sources.samples.csv.SimulationSettings;
import de.fzi.cep.sepa.sources.samples.hella.parser.MaterialMovementParser;

public class MaterialMovementStream extends AbstractHellaStream {

	public static final String montracDataFolder =System.getProperty("user.home") + File.separator +".streampipes" +File.separator +"sources" +File.separator +"data" +File.separator +"montrac" +File.separator;

	private static final List<String> fileNamePrefixes = Arrays.asList("20150910-", "20150911-", "20150912-", "20150913-", "20150914-", "20150915-", "20150916-");

	
	@Override
	public EventStream declareModel(SepDescription sep) {
		
		EventStream stream = prepareStream(HellaVariables.MontracMovement.topic());
		
		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(EpProperties.stringEp("variable_type", SO.Text));
		eventProperties.add(EpProperties.longEp("variable_timestamp", "http://schema.org/DateTime"));
		eventProperties.add(EpProperties.stringEp("location", Arrays.asList(URI.create("http://hella.de/hella#montracLocationId"), URI.create(SO.Text))));
		eventProperties.add(EpProperties.stringEp("event", Arrays.asList(URI.create("http://hella.de/hella#montracEvent"), URI.create(SO.Text))));
		eventProperties.add(EpProperties.integerEp("shuttle", Arrays.asList(URI.create("http://hella.de/hella#shuttleId"), URI.create(SO.Number))));
		eventProperties.add(EpProperties.booleanEp("rightPiece", "http://schema.org/Boolean"));
		eventProperties.add(EpProperties.stringEp("leftPiece", "http://schema.org/Boolean"));
		
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
		IMessagePublisher publisher = new ProaSenseInternalProducer(ClientConfiguration.INSTANCE.getKafkaUrl(), HellaVariables.MontracMovement.topic());
		
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
