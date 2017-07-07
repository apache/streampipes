package org.streampipes.manager.matching;

import org.streampipes.pe.algorithms.esper.extract.ProjectController;
import org.streampipes.manager.matching.v2.TestUtils;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.impl.output.CustomOutputStrategy;
import org.streampipes.pe.sources.samples.random.RandomDataProducer;
import org.streampipes.pe.sources.samples.random.RandomNumberStreamJson;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by riemer on 02.09.2016.
 */
public class TestRdfId {

    @Test
    public void testGraphIdAfterClone() {

        SepaInvocation invocation = TestUtils.makeSepa(new ProjectController(), "A", "B");
        EventStream stream = TestUtils.makeStream(new RandomDataProducer(), new RandomNumberStreamJson(), "B");

        EventStream clonedStream = new EventStream(stream);

        assertEquals(stream.getElementId(), clonedStream.getElementId());

        CustomOutputStrategy strategy = (CustomOutputStrategy) invocation.getOutputStrategies().get(0);
        strategy.setEventProperties(clonedStream.getEventSchema().getEventProperties());

        assertEquals(clonedStream.getEventSchema().getEventProperties().get(0).getElementId(), strategy.getEventProperties().get(0).getElementId());

        SepaInvocation invocation2 = new SepaInvocation(invocation);

        CustomOutputStrategy strategy2 = (CustomOutputStrategy) invocation2.getOutputStrategies().get(0);

        assertEquals(clonedStream.getEventSchema().getEventProperties().get(0).getElementId(), strategy2.getEventProperties().get(0).getElementId());



    }
}
