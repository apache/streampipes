package de.fzi.cep.sepa.manager.matching;

import junit.framework.TestCase;

import org.junit.Test;

import de.fzi.cep.sepa.esper.aggregate.avg.AggregationController;
import de.fzi.cep.sepa.manager.matching.v2.TestUtils;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.sources.samples.random.RandomDataProducer;
import de.fzi.cep.sepa.sources.samples.random.RandomNumberStreamJson;

public class TestPipelineValidationHandler extends TestCase {

	@Test
	public void testPositivePipelineValidation() {
		
		Pipeline pipeline = TestUtils.makePipeline(new RandomDataProducer(), 
				new RandomNumberStreamJson(), 
				new AggregationController());
		
		PipelineValidationHandler handler;
		try {
			handler = new PipelineValidationHandler(pipeline, true);
			handler.validateConnection();
		} catch (Exception e2) {
			fail(e2.getMessage());
		}
		
		assertTrue(true);
	}
}
