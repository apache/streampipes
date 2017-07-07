package de.fzi.cep.sepa.manager.matching;

import de.fzi.cep.sepa.esper.geo.geofencing.GeofencingController;
import de.fzi.cep.sepa.model.client.exception.InvalidConnectionException;
import junit.framework.TestCase;
import static de.fzi.cep.sepa.manager.ThrowableCaptor.captureThrowable;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

import de.fzi.cep.sepa.esper.aggregate.avg.AggregationController;
import de.fzi.cep.sepa.manager.matching.v2.TestUtils;
import de.fzi.cep.sepa.model.client.pipeline.Pipeline;
import de.fzi.cep.sepa.sources.samples.random.RandomDataProducer;
import de.fzi.cep.sepa.sources.samples.random.RandomNumberStreamJson;

public class TestPipelineValidationHandler extends TestCase {

	@Test
	public void testPositivePipelineValidation() {

		Pipeline pipeline = TestUtils.makePipeline(new RandomDataProducer(),
				new RandomNumberStreamJson(),
				new AggregationController());

		PipelineVerificationHandler handler;
		try {
			handler = new PipelineVerificationHandler(pipeline);
			handler.validateConnection();
		} catch (Exception e2) {
			fail(e2.getMessage());
		}

		assertTrue(true);
	}

	@Test
	public void testNegativePipelineValidation() {

		Pipeline pipeline = TestUtils.makePipeline(new RandomDataProducer(),
				new RandomNumberStreamJson(),
				new GeofencingController());

		PipelineVerificationHandler handler = null;


		try {
			handler = new PipelineVerificationHandler(pipeline);
		} catch (Exception e) {
			assertTrue(false);
		}

		Throwable actual = captureThrowable(handler::validateConnection);

		assertThat(actual).isInstanceOf(InvalidConnectionException.class);

	}
}
