package de.fzi.cep.sepa.manager.matching.v2;

import junit.framework.TestCase;

import org.junit.Test;

import de.fzi.cep.sepa.esper.aggregate.avg.AggregationController;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.sources.samples.random.RandomDataProducer;

public class TestElementVerification extends TestCase {

	@Test
	public void testPositive() {
		
		RandomDataProducer producer = new RandomDataProducer();
		EventStream offer = producer.getEventStreams().get(0).declareModel(producer.declareModel());
		
		SepaDescription requirement = (new AggregationController().declareModel());
		
		ElementVerification verifier = new ElementVerification();
		boolean match = verifier.verify(offer, requirement);
		
		verifier.getErrorLog().forEach(e -> System.out.println(e.getTitle()));
		assertTrue(match);
		
	}
}
