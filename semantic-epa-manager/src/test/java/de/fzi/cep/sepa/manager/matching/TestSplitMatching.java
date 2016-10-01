package de.fzi.cep.sepa.manager.matching;

import de.fzi.cep.sepa.esper.geo.geofencing.GeofencingController;
import de.fzi.cep.sepa.manager.matching.v2.TestUtils;
import de.fzi.cep.sepa.model.client.pipeline.Pipeline;
import de.fzi.cep.sepa.sources.samples.random.RandomDataProducer;
import de.fzi.cep.sepa.sources.samples.random.RandomNumberStreamJson;
import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * Created by riemer on 29.09.2016.
 */
public class TestSplitMatching {

    @Test
    public void testSplitValidation() {
        Pipeline pipeline = TestUtils.makePipeline(new RandomDataProducer(),
                new RandomNumberStreamJson(),
                new GeofencingController());

        PipelineVerificationHandler handler;
        try {
            handler = new PipelineVerificationHandler(pipeline);
            handler.validateConnection();
        } catch (Exception e2) {
            fail(e2.getMessage());
        }
    }
}
