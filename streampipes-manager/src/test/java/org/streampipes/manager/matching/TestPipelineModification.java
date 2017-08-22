package org.streampipes.manager.matching;

import org.streampipes.pe.processors.esper.aggregate.avg.AggregationController;
import org.streampipes.manager.matching.v2.TestUtils;
import org.streampipes.model.client.pipeline.PipelineModificationMessage;
import org.streampipes.model.client.pipeline.Pipeline;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.pe.sources.samples.random.RandomDataProducer;
import org.streampipes.pe.sources.samples.random.RandomNumberStreamJson;
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
            message = new PipelineVerificationHandler(pipeline)
                    .validateConnection()
                    .computeMappingProperties()
                    .getPipelineModificationMessage();
        } catch (Exception e) {
            fail(e.toString());
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
            message = new PipelineVerificationHandler(pipeline)
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
