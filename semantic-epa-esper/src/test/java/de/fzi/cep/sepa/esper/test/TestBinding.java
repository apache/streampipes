package de.fzi.cep.sepa.esper.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.ontoware.rdf2go.vocabulary.XSD;

import com.clarkparsia.empire.SupportsRdfId.RdfKey;
import com.clarkparsia.empire.SupportsRdfId.URIKey;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.esper.aggregate.rate.EventRateController;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.StaticProperty;
import de.fzi.cep.sepa.model.impl.graph.SEP;
import de.fzi.cep.sepa.model.impl.graph.SEPA;
import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;
import de.fzi.cep.sepa.storage.controller.StorageManager;

public class TestBinding {

	public static void main(String[] args)
	{
		SEP sep = null;
		try {
			sep = StorageManager.INSTANCE.getStorageAPI().getSEPById("http://localhost:8089/twitter");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		EventRateController eventRateController = new EventRateController();
		SEPA test = eventRateController.declareModel();
		test.setUri("http://test");
		test.setElementId("http://test");
		test.setRdfId(new URIKey(URI.create("http://test.de")));
		
		SEPAInvocationGraph testGraph = new SEPAInvocationGraph(test);
		testGraph.setInputStreams(Utils.createList(sep.getEventStreams().get(0)));
		EventStream outputStream = new EventStream();
		
		List<EventProperty> outputProperties = new ArrayList<>();
		outputProperties.add(new EventProperty(XSD._double.toString(),
				"rate", "", de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number")));
		
		EventSchema outputSchema = new EventSchema();
		outputSchema.setEventProperties(outputProperties);
		outputStream.setEventSchema(outputSchema);
		
		EventGrounding outputGrounding = new EventGrounding(null, 61616, "tcp://localhost", null);
		outputGrounding.setTopicName("FZI.TEST");
		outputStream.setEventGrounding(outputGrounding);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		FreeTextStaticProperty text1 = new FreeTextStaticProperty("rate", "average/sec");
		text1.setValue("5");
		staticProperties.add(text1);
		FreeTextStaticProperty text2 = new FreeTextStaticProperty("output", "output every (seconds)");
		text2.setValue("10");
		staticProperties.add(text2);
		
		testGraph.setStaticProperties(staticProperties);
		testGraph.setOutputStream(outputStream);
		
		eventRateController.invokeRuntime(testGraph);
	}
}
