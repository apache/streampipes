/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
