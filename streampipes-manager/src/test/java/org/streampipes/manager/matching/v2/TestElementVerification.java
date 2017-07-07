package org.streampipes.manager.matching.v2;

import org.streampipes.model.impl.graph.SepaInvocation;
import junit.framework.TestCase;

import org.junit.Test;

import org.streampipes.pe.processors.esper.aggregate.avg.AggregationController;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.pe.sources.samples.random.RandomDataProducer;

public class TestElementVerification extends TestCase {

	@Test
	public void testPositive() {
		
		RandomDataProducer producer = new RandomDataProducer();
		EventStream offer = producer.getEventStreams().get(0).declareModel(producer.declareModel());
		
		SepaDescription requirement = (new AggregationController().declareModel());
		
		ElementVerification verifier = new ElementVerification();
		boolean match = verifier.verify(offer, new SepaInvocation(requirement));
		
		verifier.getErrorLog().forEach(e -> System.out.println(e.getTitle()));
		assertTrue(match);
		
	}
}
