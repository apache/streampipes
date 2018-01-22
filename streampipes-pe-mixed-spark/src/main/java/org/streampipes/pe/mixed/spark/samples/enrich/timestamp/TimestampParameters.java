package org.streampipes.pe.mixed.spark.samples.enrich.timestamp;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

import java.util.List;

/**
 * Created by Jochen Lutz on 2018-01-22.
 */
public class TimestampParameters extends EventProcessorBindingParams {
    private final String appendTimePropertyName;
    private final List<String> selectProperties;

    public TimestampParameters(DataProcessorInvocation graph, String appendTimePropertyName, List<String> selectProperties) {
        super(graph);
        this.appendTimePropertyName = appendTimePropertyName;
        this.selectProperties = selectProperties;
    }
}
