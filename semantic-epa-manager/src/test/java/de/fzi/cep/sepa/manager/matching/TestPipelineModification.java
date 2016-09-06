package de.fzi.cep.sepa.manager.matching;

import de.fzi.cep.sepa.esper.aggregate.avg.AggregationController;
import de.fzi.cep.sepa.manager.matching.v2.TestUtils;
import de.fzi.cep.sepa.model.client.pipeline.PipelineModificationMessage;
import de.fzi.cep.sepa.model.client.pipeline.Pipeline;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.sources.samples.random.RandomDataProducer;
import de.fzi.cep.sepa.sources.samples.random.RandomNumberStreamJson;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by riemer on 01.09.2016.
 */


public class TestPipelineModification {

    @Test
    public void testPipelineModificationMessagePresent() {

        SepaInvocation invocation = TestUtils.makeSepa(new AggregationController(), "B", "A");
        EventStream stream = TestUtils.makeStream(new RandomDataProducer(), new RandomNumberStreamJson(), "A");

        Pipeline pipeline =TestUtils.makePipeline(Arrays.asList(stream), Arrays.asList(invocation));

        PipelineModificationMessage message = null;
        try {
            message = new PipelineVerificationHandler(pipeline, true)
                    .validateConnection()
                    .computeMappingProperties()
                    .getPipelineModificationMessage();
        } catch (Exception e) {
            fail("Exception");
        }

        assertNotNull(message);
    }

    @Test
    public void testPipelineMappingProperties() {

        SepaInvocation invocation = TestUtils.makeSepa(new AggregationController(), "B", "A");
        EventStream stream = TestUtils.makeStream(new RandomDataProducer(), new RandomNumberStreamJson(), "A");

        Pipeline pipeline =TestUtils.makePipeline(Arrays.asList(stream), Arrays.asList(invocation));

        PipelineModificationMessage message = null;
        try {
            message = new PipelineVerificationHandler(pipeline, true)
                    .validateConnection()
                    .computeMappingProperties()
                    .getPipelineModificationMessage();
        } catch (Exception e) {
            fail("Exception");
        }

        assertNotNull(message);
        assertTrue(message.getPipelineModifications().size() > 0);
    }

}
