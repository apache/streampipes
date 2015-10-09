package de.fzi.cep.sepa.storm.sentiment.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.desc.EpDeclarer;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.storm.config.StormConfig;
import de.fzi.cep.sepa.storm.utils.Parameters;
import de.fzi.cep.sepa.storm.utils.Utils;
import de.fzi.cep.sepa.util.StandardTransportFormat;

public class SentimentDetectionController extends EpDeclarer<Parameters>{

	@Override
	public SepaDescription declareModel() {
		
		SepaDescription desc = new SepaDescription("storm/sentiment", "Sentiment Analysis",
				"Sentiment Analysis");
		
		desc.setIconUrl(StormConfig.iconBaseUrl + "/Sentiment_Detection_Icon_HQ.png");
		try {
			
			EventStream stream1 = new EventStream();
			EventSchema schema1 = new EventSchema();
			List<EventProperty> eventProperties = new ArrayList<EventProperty>();
			eventProperties.add(new EventPropertyPrimitive(de.fzi.cep.sepa.commons.Utils.createURI(
					SO.Text)));
			
			schema1.setEventProperties(eventProperties);
			stream1.setEventSchema(schema1);
			stream1.setUri(StormConfig.serverUrl + desc.getElementId());
			desc.addEventStream(stream1);

			List<OutputStrategy> outputStrategies = new ArrayList<OutputStrategy>();
			AppendOutputStrategy outputStrategy = new AppendOutputStrategy();

			List<EventProperty> appendProperties = new ArrayList<EventProperty>();
			appendProperties.add(new EventPropertyPrimitive(XSD._double.toString(),
					"sentiment", "", de.fzi.cep.sepa.commons.Utils.createURI("http://test.de/sentiment"))); //Um welches Property erweitert wird
			outputStrategy.setEventProperties(appendProperties);
			outputStrategies.add(outputStrategy);
			desc.setOutputStrategies(outputStrategies);
			
			List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
			staticProperties.add(new MappingPropertyUnary("sentimentMapsTo", "Sentiment Mapping"));
			desc.setStaticProperties(staticProperties);
			
			desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return desc;
	}

	@Override
	public Response invokeRuntime(SepaInvocation invocationGraph) {
		//TODO 
		// save invocation graph to couchDB
		// Upload jar to storm topology with the parameters of the invocationGrapf id, couchDB host
		String id = Utils.storeSepaInvocation(invocationGraph);
		executeCommand("/home/philipp/Downloads/apache-storm-0.9.5/bin/storm jar /home/philipp/Coding/fzi/icep/semantic-epa-parent/semantic-epa-storm-topology-sentiment-new/target/semantic-epa-storm-topology-sentiment-new-0.0.1-SNAPSHOT.jar de.fzi.cep.sepa.storm.sentiment.topology.Main "+ id +" test -c nimbus.host=ipe-koi05.fzi.de -c nimbus.thift.port=49627");
		
//		String sentimentMapsTo = SepaUtils.getMappingPropertyName(invocationGraph, "sentimentMapsTo");

//		SentimentDetectionParameters params = new SentimentDetectionParameters(new SepaInvocation(invocationGraph), sentimentMapsTo);
//		ConfigurationMessage<SentimentDetectionParameters> msg = new ConfigurationMessage<>(Operation.BIND, params);
		
		return null;
	}
	
	private String executeCommand(String command) {

		StringBuffer output = new StringBuffer();

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = 
                            new BufferedReader(new InputStreamReader(p.getInputStream()));

                        String line = "";			
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return output.toString();

	}

//	protected String getKafkaUrl() {
//		return "ipe-koi04.fzi.de:9092";
//	}

}
