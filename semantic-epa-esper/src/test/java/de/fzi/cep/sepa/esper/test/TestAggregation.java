package de.fzi.cep.sepa.esper.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.clarkparsia.empire.SupportsRdfId.URIKey;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.esper.aggregate.avg.AggregationController;
import de.fzi.cep.sepa.esper.aggregate.rate.EventRateController;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.MappingProperty;
import de.fzi.cep.sepa.model.impl.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.StaticProperty;
import de.fzi.cep.sepa.model.impl.graph.SEP;
import de.fzi.cep.sepa.model.impl.graph.SEPA;
import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.storage.controller.StorageManager;

public class TestAggregation {

	public static void main(String[] args)
	{
		SEP sep = null;
		try {
			sep = StorageManager.INSTANCE.getStorageAPI().getSEPById("http://localhost:8089/random");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AggregationController eventRateController = new AggregationController();
		SEPA test = eventRateController.declareModel();
		test.setUri("http://test");
		test.setElementId("http://test");
		test.setRdfId(new URIKey(URI.create("http://test.de")));
		
		SEPAInvocationGraph testGraph = new SEPAInvocationGraph(test);
		testGraph.setInputStreams(Utils.createList(sep.getEventStreams().get(0)));
		EventStream outputStream = new EventStream();
		
		List<EventProperty> outputProperties = new ArrayList<>();
		outputProperties.add(new EventPropertyPrimitive(XSD._double.toString(),
				"averageValue", "", de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number")));
		
		EventSchema outputSchema = new EventSchema();
		outputSchema.setEventProperties(outputProperties);
		outputStream.setEventSchema(outputSchema);
		
		EventGrounding outputGrounding = new EventGrounding(null, 61616, "tcp://localhost", null);
		outputGrounding.setTopicName("FZI.TEST");
		outputStream.setEventGrounding(outputGrounding);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		MappingPropertyUnary mp = new MappingPropertyUnary(URI.create(test.getEventStreams().get(0).getEventSchema().getEventProperties().get(0).getElementId()), "groupBy", "group stream by: ");
		mp.setMapsTo(URI.create(sep.getEventStreams().get(0).getEventSchema().getEventProperties().get(1).getElementId()));
		staticProperties.add(mp);
		
		FreeTextStaticProperty text1 = new FreeTextStaticProperty("outputEvery", "output values every (seconds)");
		text1.setValue("5");
		staticProperties.add(text1);
		FreeTextStaticProperty text2 = new FreeTextStaticProperty("timeWindow", "output values every (seconds)");
		text2.setValue("10");
		staticProperties.add(text2);
		
		testGraph.setStaticProperties(staticProperties);
		testGraph.setOutputStream(outputStream);
		
		eventRateController.invokeRuntime(testGraph);
	}
}
