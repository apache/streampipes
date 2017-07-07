package de.fzi.cep.sepa.storm.sentiment.controller;

import de.fzi.cep.sepa.client.util.StandardTransportFormat;
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
import de.fzi.cep.sepa.runtime.camel.declarer.CamelEpDeclarer;
import de.fzi.cep.sepa.storm.config.StormConfig;
import de.fzi.cep.sepa.storm.sentiment.topology.Name;
import de.fzi.cep.sepa.storm.utils.Parameters;
import de.fzi.cep.sepa.storm.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class SentimentDetectionController extends CamelEpDeclarer<Parameters>{
	private static String STORM_LOCATION = "/apache-storm-0.9.5/bin/storm";
	private static String JAR_LOCATION = "/semantic-epa-storm-topology-sentiment.jar";
	private static String MAIN_CLASS = "de.fzi.cep.sepa.storm.sentiment.topology.Main";

	private static String ID;
	private static String REV;


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
			staticProperties.add(new MappingPropertyUnary("sentimentMapsTo", "Sentiment Mapping", ""));
			desc.setStaticProperties(staticProperties);
			
			desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return desc;
	}

	@Override
	public Response invokeRuntime(SepaInvocation invocationGraph) {
		org.lightcouch.Response res = Utils.storeSepaInvocation(new SepaInvocation(invocationGraph));
		ID = res.getId();
		REV = res.getRev();

        System.out.println("Start uploading the topology!");
		Utils.executeCommand(STORM_LOCATION + " jar " + JAR_LOCATION + " " + MAIN_CLASS +" "+ ID +" -c nimbus.host=" + Utils.NIMBUS_HOST + " -c nimbus.thift.port=" + Utils.NIMBUS_THRIFT_PORT);
        System.out.println("The topology was successfully uploaded!");
		
		return new Response(ID, true);
	}
	
	@Override 
	public Response detachRuntime(String elementId) {
		Utils.executeCommand(STORM_LOCATION + " kill " + Name.getTopologyName() +" -c nimbus.host=" + Utils.NIMBUS_HOST + " -c nimbus.thift.port=" + Utils.NIMBUS_THRIFT_PORT);
		Utils.removeSepaInvocation(ID, REV);

		return new Response(ID, true);
	}
	
}
