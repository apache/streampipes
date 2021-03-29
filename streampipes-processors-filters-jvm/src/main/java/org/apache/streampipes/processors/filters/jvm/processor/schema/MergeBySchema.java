package org.apache.streampipes.processors.filters.jvm.processor.schema;

import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventFactory;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.runtime.EventProcessor;

import java.util.List;

public class MergeBySchema implements EventProcessor<MergeBySchemaParameters> {


    private EventSchema outputSchema;
    private List<String> outputKeySelectors;


    private StreamBuffer streamBufferS0;
    private StreamBuffer streamBufferS1;

    @Override
    public void onInvocation(MergeBySchemaParameters composeParameters, SpOutputCollector spOutputCollector, EventProcessorRuntimeContext runtimeContext) {
        this.outputSchema = composeParameters.getGraph().getOutputStream().getEventSchema();
        this.outputKeySelectors = composeParameters.getOutputKeySelectors();

        this.streamBufferS0 = new StreamBuffer();
        this.streamBufferS1 = new StreamBuffer();
    }


    @Override
    public void onEvent(Event event, SpOutputCollector spOutputCollector) {
        String streamId = event.getSourceInfo().getSelectorPrefix();

        if ("s0".equals(streamId)) {
            this.streamBufferS0.add(event);
        } else {
            this.streamBufferS1.add(event);
        }

        for (Event e0 : this.streamBufferS0.getList()) {
            for (Event e1 : this.streamBufferS1.getList()) {
                if (e0.getSchemaInfo().equals(e1.getSchemaInfo())) {
                    Event resultingEvent = mergeEvents(e0, e1);
                    spOutputCollector.collect(resultingEvent);
                    this.streamBufferS0.removeOldEvent(e0);
                    this.streamBufferS1.removeOldEvent(e1);
                }
            }
        }
    }

    @Override
    public void onDetach() {
        this.streamBufferS0.reset();
        this.streamBufferS1.reset();
    }

    private Event mergeEvents(Event e1, Event e2) {
        return EventFactory.fromEvents(e1, e2, outputSchema).getSubset(outputKeySelectors);
    }

}
