package de.fzi.cep.sepa.sources.samples.taxi.test;

import java.net.URISyntaxException;

import javax.jms.JMSException;

import org.apache.commons.collections.MapUtils;
import org.openrdf.model.Graph;
import org.openrdf.model.impl.GraphImpl;
import org.openrdf.rio.RDFHandlerException;

import scala.actors.threadpool.Arrays;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.desc.EventStreamDeclarer;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventPropertyList;
import de.fzi.cep.sepa.model.impl.EventPropertyNested;
import de.fzi.cep.sepa.model.impl.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SEP;
import de.fzi.cep.sepa.model.impl.graph.SEPA;
import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;
import de.fzi.cep.sepa.sources.samples.random.RandomDataProducer;
import de.fzi.cep.sepa.sources.samples.random.RandomNumberStream;
import de.fzi.cep.sepa.sources.samples.twitter.TwitterStreamProducer;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.cep.sepa.storage.util.Transformer;

public class TestRandomNumberStream {

	public static void main(String[] args) throws JMSException, IllegalArgumentException, IllegalAccessException, SecurityException, RDFHandlerException, URISyntaxException
	{
		
		RandomDataProducer producer = new RandomDataProducer();
	
		SEP sep = producer.declareModel();
		
		for (EventStreamDeclarer declarer : producer.getEventStreams()) {
			EventStream stream = declarer.declareModel(sep);
			// stream.setUri(baseUri + stream.getUri());
			sep.addEventStream(stream);
		}
		
		Graph graph = Transformer.generateCompleteGraph(new GraphImpl(), sep);
		
		System.out.println(Utils.asString(graph));
		
		SEP stream2 = Transformer.fromJsonLd(SEP.class, Utils.asString(graph));
		
		for(EventProperty p : stream2.getEventStreams().get(1).getEventSchema().getEventProperties())
		{
			if (p instanceof EventPropertyPrimitive)
			{
				System.out.println("primitive, " +p.getRuntimeName());
			}
			else if (p instanceof EventPropertyNested)
			{
				System.out.println("nested, " +p.getRuntimeName());
				EventPropertyNested nestedProperty = (EventPropertyNested) p;
				for(EventProperty np : nestedProperty.getEventProperties())
				{
					System.out.println(p.getRuntimeName() +", " +np.getRuntimeName());
				}
			}
			else if (p instanceof EventPropertyList)
			{
				System.out.println("list, " +p.getRuntimeName());
				//EventProperty sub = ((EventPropertyList) p).getEventProperty();
				//System.out.println(sub.getPropertyName());
			}
		}
		
		SEPA sepa = StorageManager.INSTANCE.getStorageAPI().getSEPAById("http://localhost:8090/sepa/numericalfilter");
		SEPAInvocationGraph sepaInvocationGraph = new SEPAInvocationGraph(sepa);
		sepaInvocationGraph.setInputStreams(Utils.createList(stream2.getEventStreams().get(1)));
		
		Graph graph2 = Transformer.generateCompleteGraph(new GraphImpl(), sepaInvocationGraph);
		
		System.out.println(Utils.asString(graph2));
		
		MapUtils.debugPrint(System.out, "label", stream2.getEventStreams().get(1).getEventSchema().toRuntimeMap());
		/*
		Map<String, Object> testMap = stream2.getEventStreams().get(0).getEventSchema().toRuntimeMap();
		Iterator it = testMap.keySet().iterator();
		while(it.hasNext()) System.out.println(it.next().toString());
		*/
	}
}
