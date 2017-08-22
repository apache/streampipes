package org.streampipes.pe.processors.storm.controller;

import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.impl.output.AppendOutputStrategy;
import org.streampipes.model.impl.output.OutputStrategy;
import org.streampipes.model.impl.staticproperty.MappingPropertyUnary;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.vocabulary.SO;
import org.streampipes.model.vocabulary.XSD;
import de.fzi.cep.sepa.runtime.camel.declarer.CamelEpDeclarer;
import de.fzi.cep.sepa.storm.config.StormConfig;
import org.streampipes.pe.processors.storm.topology.Name;
import de.fzi.cep.sepa.storm.utils.Parameters;
import de.fzi.cep.sepa.storm.utils.Utils;
import org.streampipes.commons.Utils;

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
			eventProperties.add(new EventPropertyPrimitive(Utils.createURI(
					SO.Text)));
			
			schema1.setEventProperties(eventProperties);
			stream1.setEventSchema(schema1);
			stream1.setUri(StormConfig.serverUrl + desc.getElementId());
			desc.addEventStream(stream1);

			List<OutputStrategy> outputStrategies = new ArrayList<OutputStrategy>();
			AppendOutputStrategy outputStrategy = new AppendOutputStrategy();

			List<EventProperty> appendProperties = new ArrayList<EventProperty>();
			appendProperties.add(new EventPropertyPrimitive(XSD._double.toString(),
					"sentiment", "", Utils.createURI("http://test.de/sentiment"))); //Um welches Property erweitert wird
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
