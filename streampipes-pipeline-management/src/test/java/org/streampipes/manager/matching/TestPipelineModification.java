package org.streampipes.manager.matching;

import static org.junit.Assert.*;

import org.junit.Test;
import org.streampipes.manager.matching.v2.TestUtils;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.client.pipeline.Pipeline;
import org.streampipes.model.client.pipeline.PipelineModificationMessage;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.pe.processors.esper.aggregate.avg.AggregationController;
import org.streampipes.pe.sources.samples.random.RandomDataProducer;
import org.streampipes.pe.sources.samples.random.RandomNumberStreamJson;

import java.util.Arrays;

public class TestPipelineModification {

    @Test
    public void testPipelineModificationMessagePresent() {

        DataProcessorInvocation invocation = TestUtils.makeSepa(new AggregationController(), "B", "A");
        SpDataStream stream = TestUtils.makeStream(new RandomDataProducer(), new RandomNumberStreamJson(), "A");

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

        DataProcessorInvocation invocation = TestUtils.makeSepa(new AggregationController(), "B", "A");
        SpDataStream stream = TestUtils.makeStream(new RandomDataProducer(), new RandomNumberStreamJson(), "A");

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
