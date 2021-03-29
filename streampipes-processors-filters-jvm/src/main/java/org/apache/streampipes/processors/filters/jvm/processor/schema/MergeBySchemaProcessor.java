package org.apache.streampipes.processors.filters.jvm.processor.schema;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventFactory;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.standalone.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import java.util.List;

public class MergeBySchemaProcessor extends StreamPipesDataProcessor {

    List<String> outputKeySelectors;
    private StreamBuffer streamBufferS0;
    private StreamBuffer streamBufferS1;
    private EventSchema outputSchema;
    private static final String SELECT_STREAM = "select-stream";
    private String selectedStream;


    @Override
    public DataProcessorDescription declareModel() {
        return ProcessingElementBuilder.create("org.apache.streampipes.processors.filters.jvm.schema")
                .category(DataProcessorType.TRANSFORM)
                .withAssets(Assets.DOCUMENTATION, Assets.ICON)
                .withLocales(Locales.EN)
                .outputStrategy(OutputStrategies.custom(true))
                .requiredSingleValueSelection(Labels.withId(SELECT_STREAM),
                        Options.from("Stream 1", "Stream 2"))
                .build();
    }

    @Override
    public void onInvocation(ProcessorParams processorParams, SpOutputCollector spOutputCollector, EventProcessorRuntimeContext eventProcessorRuntimeContext) throws SpRuntimeException {
        this.outputKeySelectors = processorParams.extractor().outputKeySelectors();
        this.outputSchema = processorParams.getGraph().getOutputStream().getEventSchema();
        this.streamBufferS0 = new StreamBuffer();
        this.streamBufferS1 = new StreamBuffer();
        if (processorParams.extractor().selectedSingleValue(SELECT_STREAM, String.class).equals("Stream 1")) {
            this.selectedStream = "s0";
        } else {
            this.selectedStream = "s1";
        }

    }

    @Override
    public void onEvent(Event event, SpOutputCollector spOutputCollector) throws SpRuntimeException {

        String streamId = event.getSourceInfo().getSelectorPrefix();

        if (this.selectedStream.equals(streamId)) {
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

    private Event mergeEvents(Event e1, Event e2) {
        return EventFactory.fromEvents(e1, e2, outputSchema).getSubset(outputKeySelectors);
    }

    @Override
    public void onDetach() throws SpRuntimeException {
        this.streamBufferS0.reset();
        this.streamBufferS1.reset();

    }
}
