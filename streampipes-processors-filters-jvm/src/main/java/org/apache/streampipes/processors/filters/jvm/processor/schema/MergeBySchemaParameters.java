package org.apache.streampipes.processors.filters.jvm.processor.schema;

import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.wrapper.params.binding.EventProcessorBindingParams;

import java.util.List;

public class MergeBySchemaParameters extends EventProcessorBindingParams {
    private List<String> outputKeySelectors;


    public MergeBySchemaParameters(DataProcessorInvocation graph, List<String> outputKeySelectors) {
        super(graph);
        this.outputKeySelectors = outputKeySelectors;

    }

    public List<String> getOutputKeySelectors() {
        return outputKeySelectors;
    }


}
