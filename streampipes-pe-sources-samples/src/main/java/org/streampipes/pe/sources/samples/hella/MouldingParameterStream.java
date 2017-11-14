package org.streampipes.pe.sources.samples.hella;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.streampipes.commons.Utils;
import org.streampipes.messaging.EventProducer;
import org.streampipes.messaging.kafka.SpKafkaProducer;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.vocabulary.SO;
import org.streampipes.vocabulary.XSD;
import org.streampipes.pe.sources.samples.adapter.CsvPublisher;
import org.streampipes.pe.sources.samples.adapter.CsvReadingTask;
import org.streampipes.pe.sources.samples.adapter.FolderReadingTask;
import org.streampipes.pe.sources.samples.adapter.LineParser;
import org.streampipes.pe.sources.samples.adapter.SimulationSettings;
import org.streampipes.pe.sources.samples.config.SourcesConfig;
import org.streampipes.pe.sources.samples.hella.parser.MouldingParametersParser;

public class MouldingParameterStream extends AbstractHellaStream {

	public static final String mouldingDataFolder =System.getProperty("user.home") + File.separator +".streampipes" +File.separator +"sources" +File.separator +"data" +File.separator +"imm" +File.separator;

	private static final List<String> fileNamePrefixes = Arrays.asList("IMM_61282649_2015091", "IMM_61282649_2015091", "IMM_61282649_2015091", "IMM_61282649_2015091", "IMM_61282649_2015091", "IMM_61282649_2015091", "IMM_61282649_2015091");

	
	@Override
	public SpDataStream declareModel(DataSourceDescription sep) {
		
		SpDataStream stream = prepareStream(HellaVariables.IMM.topic());
		
		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "variable_type", "", Utils.createURI(SO.Text)));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "variable_timestamp", "", Utils.createURI("http://schema.org/DateTime")));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "machineId", "", Utils.createURI(SO.Text)));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "movementDifferential", "", Utils.createURI(SO.Number)));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "meltCushion", "", Utils.createURI(SO.Number)));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "jetTemperation", "", Utils.createURI(SO.Number)));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "dosingTime", "", Utils.createURI(SO.Number)));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "injectionTime", "", Utils.createURI(SO.Number)));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "cycleTime", "", Utils.createURI(SO.Number)));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "cavityPressure", "", Utils.createURI(SO.Number)));
	
		
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setName(HellaVariables.IMM.eventName());
		stream.setDescription(HellaVariables.IMM.description());
		stream.setUri(sep.getUri() + "/moulding");
		
		return stream;
	}

	@Override
	public void executeStream() {
		
		EventProducer publisher = new SpKafkaProducer(SourcesConfig.INSTANCE.getKafkaUrl(), HellaVariables.IMM
            .topic());
		
		//IMessagePublisher publisher = new ConsoleLoggingPublisher();
		
		LineParser mouldingLineParser = new MouldingParametersParser();
		CsvReadingTask csvReadingTask = new CsvReadingTask(makeFolderReadingTasks(), ";", "variable_timestamp", mouldingLineParser, true);
				
		Thread mouldingReplayThread = new Thread(new CsvPublisher(publisher, csvReadingTask, SimulationSettings.DEMONSTRATE_10));
		mouldingReplayThread.start();
	}

	private List<FolderReadingTask> makeFolderReadingTasks() {
		List<FolderReadingTask> tasks = new ArrayList<>();
		
		for(String fileNamePrefix : fileNamePrefixes)
		{
			FolderReadingTask task = new FolderReadingTask(mouldingDataFolder, fileNamePrefix, "csv", 0, 6);
			tasks.add(task);
		}
		return tasks;
	}

	@Override
	public boolean isExecutable() {
		return true;
	}


}
