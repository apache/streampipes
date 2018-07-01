package org.streampipes.processors.pattern.detection.flink.processor.and;

import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.processors.pattern.detection.flink.config.PatternDetectionFlinkConfig;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

import java.util.ArrayList;
import java.util.List;

public class AndController extends FlinkDataProcessorDeclarer<AndParameters> {

  @Override
  public DataProcessorDescription declareModel() {
    return null;
  }


  @Override
  public FlinkDataProcessorRuntime<AndParameters> getRuntime(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {
    String timeUnit = SepaUtils.getOneOfProperty(graph, "time-unit");
    //String matchingOperator = SepaUtils.getOneOfProperty(invocationGraph, "matching-operator");
    String matchingOperator = "";
    int duration = extractor.singleValueParameter("duration", Integer.class);
    //String partitionProperty = SepaUtils.getMappingPropertyName(invocationGraph, "partition", true);
    //List<String> matchingProperties = SepaUtils.getMatchingPropertyNames(invocationGraph, "matching");
    List<String> matchingProperties = new ArrayList<>();
    AndParameters params = new AndParameters(graph, timeUnit, matchingOperator, duration, matchingProperties);

    return new AndProgram(params, PatternDetectionFlinkConfig.INSTANCE.getDebug());

  }
}
