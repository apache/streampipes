package de.fzi.cep.sepa.sources.samples.taxi.test;

import java.util.Iterator;
import java.util.Map;

import javax.jms.JMSException;

import org.apache.commons.collections.MapUtils;
import org.openrdf.model.Graph;
import org.openrdf.model.impl.GraphImpl;
import org.openrdf.rio.RDFHandlerException;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.desc.EventStreamDeclarer;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventPropertyNested;
import de.fzi.cep.sepa.model.impl.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SEP;
import de.fzi.cep.sepa.sources.samples.taxi.NYCTaxiProducer;
import de.fzi.cep.sepa.sources.samples.twitter.TwitterStreamProducer;
import de.fzi.cep.sepa.storage.util.Transformer;

public class TestTaxiStream {

	public static void main(String[] args) throws JMSException, IllegalArgumentException, IllegalAccessException, SecurityException, RDFHandlerException
	{
		
		TwitterStreamProducer producer = new TwitterStreamProducer();
	
		SEP sep = producer.declareModel();
		
		for (EventStreamDeclarer declarer : producer.getEventStreams()) {
			EventStream stream = declarer.declareModel(sep);
			// stream.setUri(baseUri + stream.getUri());
			sep.addEventStream(stream);
		}
		
		Graph graph = Transformer.generateCompleteGraph(new GraphImpl(), sep);
		
		System.out.println(Utils.asString(graph));
		
		SEP stream2 = Transformer.fromJsonLd(SEP.class, Utils.asString(graph));
		
		for(EventProperty p : stream2.getEventStreams().get(0).getEventSchema().getEventProperties())
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
		}
		
		MapUtils.debugPrint(System.out, "label", stream2.getEventStreams().get(0).getEventSchema().toRuntimeMap());
		
		/*
		Map<String, Object> testMap = stream2.getEventStreams().get(0).getEventSchema().toRuntimeMap();
		Iterator it = testMap.keySet().iterator();
		while(it.hasNext()) System.out.println(it.next().toString());
		*/
	}
}
