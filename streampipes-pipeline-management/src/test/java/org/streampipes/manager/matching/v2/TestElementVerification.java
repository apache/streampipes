package org.streampipes.manager.matching.v2;

import org.streampipes.model.graph.DataProcessorInvocation;
import junit.framework.TestCase;

import org.junit.Test;

import org.streampipes.pe.processors.esper.aggregate.avg.AggregationController;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.pe.sources.samples.random.RandomDataProducer;

public class TestElementVerification extends TestCase {

	@Test
	public void testPositive() {
		
		RandomDataProducer producer = new RandomDataProducer();
		SpDataStream offer = producer.getEventStreams().get(0).declareModel(producer.declareModel());
		
		DataProcessorDescription requirement = (new AggregationController().declareModel());
		
		ElementVerification verifier = new ElementVerification();
		boolean match = verifier.verify(offer, new DataProcessorInvocation(requirement));
		
		verifier.getErrorLog().forEach(e -> System.out.println(e.getTitle()));
		assertTrue(match);
		
	}
}
