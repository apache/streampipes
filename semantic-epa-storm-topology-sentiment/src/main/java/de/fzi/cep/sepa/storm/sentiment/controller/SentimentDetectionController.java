package de.fzi.cep.sepa.storm.sentiment.controller;

import java.util.ArrayList;
import java.util.List;

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
import de.fzi.cep.sepa.storm.controller.AbstractStormController;
import de.fzi.cep.sepa.storm.controller.ConfigurationMessage;
import de.fzi.cep.sepa.storm.controller.Operation;
import de.fzi.cep.sepa.storm.sentiment.config.StormConfig;

public class SentimentDetectionController extends AbstractStormController<SentimentDetectionParameters>{

	@Override
	public SepaDescription declareModel() {
		
		SepaDescription desc = new SepaDescription("/storm/sentiment", "Sentiment Analysis",
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

		} catch (Exception e) {
			e.printStackTrace();
		}
		return desc;
	}

	@Override
	public Response invokeRuntime(SepaInvocation invocationGraph) {
		String sentimentMapsTo = "test"; //SepaUtils.getMappingPropertyName(invocationGraph, "sentimentMapsTo");
		
		SentimentDetectionParameters params = new SentimentDetectionParameters(invocationGraph, sentimentMapsTo);
		ConfigurationMessage<SentimentDetectionParameters> msg = new ConfigurationMessage<>(Operation.BIND, params);
		
		return prepareTopology(msg);
	}

	@Override
	protected String getKafkaUrl() {
		return "kalmar39.fzi.de:9092";
	}

}
