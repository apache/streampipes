package org.streampipes.manager.matching;

import org.streampipes.pe.processors.esper.geo.geofencing.GeofencingController;
import org.streampipes.model.client.exception.InvalidConnectionException;
import junit.framework.TestCase;

import org.junit.Test;
//import static org.assertj.core.api.Assertions.assertThat;


import org.streampipes.pe.processors.esper.aggregate.avg.AggregationController;
import org.streampipes.manager.matching.v2.TestUtils;
import org.streampipes.model.client.pipeline.Pipeline;
import org.streampipes.pe.sources.samples.random.RandomDataProducer;
import org.streampipes.pe.sources.samples.random.RandomNumberStreamJson;
import org.streampipes.manager.ThrowableCaptor;

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

		Throwable actual = ThrowableCaptor.captureThrowable(handler::validateConnection);

		//assertThat(actual).isInstanceOf(InvalidConnectionException.class);

	}
}
