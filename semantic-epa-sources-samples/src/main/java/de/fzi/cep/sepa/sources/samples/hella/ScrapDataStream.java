package de.fzi.cep.sepa.sources.samples.hella;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.commons.messaging.ConsoleLoggingPublisher;
import de.fzi.cep.sepa.commons.messaging.IMessagePublisher;
import de.fzi.cep.sepa.commons.messaging.ProaSenseInternalProducer;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.sources.samples.csv.CsvPublisher;
import de.fzi.cep.sepa.sources.samples.csv.CsvReadingTask;
import de.fzi.cep.sepa.sources.samples.csv.FolderReadingTask;
import de.fzi.cep.sepa.sources.samples.csv.LineParser;
import de.fzi.cep.sepa.sources.samples.csv.SimulationSettings;
import de.fzi.cep.sepa.sources.samples.hella.parser.ScrapLineParser;


public class ScrapDataStream extends AbstractHellaStream {
	
	private static final String scrapDataFolder = "E:\\hella-data\\hella-1week\\week-dummy-scrap\\Scrap_2015_09_7_days";
	private static final List<String> fileNamePrefixes = Arrays.asList("20150910_", "20150911_", "20150912_", "20150913_", "20150914_", "20150915_", "20150916_");

	@Override
	public EventStream declareModel(SepDescription sep) {
		
		EventStream stream = prepareStream(HellaVariables.Scrap.topic());
		
		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "variable_type", "", Utils.createURI(SO.Text)));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "variable_timestamp", "", Utils.createURI("http://schema.org/DateTime")));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "machineId", "", Utils.createURI(SO.Text)));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "type", "", Utils.createURI(SO.Text)));
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "scrap", "", Utils.createURI(SO.Number)));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "reasonText", "", Utils.createURI(SO.Text)));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "designation", "", Utils.createURI(SO.Text)));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "finalArticle", "", Utils.createURI(SO.Text)));
	
		
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setName(HellaVariables.Scrap.eventName());
		stream.setDescription(HellaVariables.Scrap.description());
		stream.setUri(sep.getUri() + "/scrap");
		
		return stream;
	}

	@Override
	public void executeStream() {
		
		//IMessagePublisher publisher = new ProaSenseInternalProducer(ClientConfiguration.INSTANCE.getKafkaUrl(), HellaVariables.Scrap.topic());
		
		IMessagePublisher publisher = new ConsoleLoggingPublisher();
		
		LineParser scrapLineParser = new ScrapLineParser();
		CsvReadingTask csvReadingTask = new CsvReadingTask(makeFolderReadingTasks(), ";", "variable_timestamp", scrapLineParser, true);
				
		Thread scrapReplayThread = new Thread(new CsvPublisher(publisher, csvReadingTask, SimulationSettings.PERFORMANCE_TEST));
		scrapReplayThread.start();
	}

	private List<FolderReadingTask> makeFolderReadingTasks() {
		List<FolderReadingTask> tasks = new ArrayList<>();
		
		for(String fileNamePrefix : fileNamePrefixes)
		{
			FolderReadingTask task = new FolderReadingTask(scrapDataFolder, fileNamePrefix, "csv", 1, 3);
			tasks.add(task);
		}
		return tasks;
	}

	@Override
	public boolean isExecutable() {
		return true;
	}
	
	
}
