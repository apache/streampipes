package org.streampipes.pe.sources.samples.hella;

import org.streampipes.commons.Utils;
import org.streampipes.messaging.EventProducer;
import org.streampipes.messaging.kafka.SpKafkaProducer;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.vocabulary.SO;
import org.streampipes.vocabulary.XSD;
import org.streampipes.pe.sources.samples.adapter.CsvPublisher;
import org.streampipes.pe.sources.samples.adapter.CsvReadingTask;
import org.streampipes.pe.sources.samples.adapter.FolderReadingTask;
import org.streampipes.pe.sources.samples.adapter.LineParser;
import org.streampipes.pe.sources.samples.adapter.SimulationSettings;
import org.streampipes.pe.sources.samples.config.SourcesConfig;
import org.streampipes.pe.sources.samples.hella.parser.ScrapLineParser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ScrapDataStream extends AbstractHellaStream {
	
	public static final String scrapDataFolder =System.getProperty("user.home") + File.separator +".streampipes" +File.separator +"sources" +File.separator +"data" +File.separator +"scrap" +File.separator;
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
		
		EventProducer publisher = new SpKafkaProducer(SourcesConfig.INSTANCE.getKafkaUrl(), HellaVariables.Scrap
            .topic());
		
		//IMessagePublisher publisher = new ConsoleLoggingPublisher();
		
		LineParser scrapLineParser = new ScrapLineParser();
		CsvReadingTask csvReadingTask = new CsvReadingTask(makeFolderReadingTasks(), ";", "variable_timestamp", scrapLineParser, true);
				
		Thread scrapReplayThread = new Thread(new CsvPublisher(publisher, csvReadingTask, SimulationSettings.DEMONSTRATE_10));
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
