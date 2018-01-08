package org.streampipes.wrapper.spark;

import org.streampipes.container.declarer.SemanticEventProcessingAgentDeclarer;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

/**
 * Created by Jochen Lutz on 2017-11-28.
 */
public abstract class SparkDataProcessorDeclarer<B extends EventProcessorBindingParams>
    extends AbstractSparkDeclarer<DataProcessorDescription, DataProcessorInvocation, SparkDataProcessorRuntime> implements SemanticEventProcessingAgentDeclarer {
}
