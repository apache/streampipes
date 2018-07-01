package org.streampipes.processors.pattern.detection.flink.processor.absence;

import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.model.DataProcessorType;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.output.CustomOutputStrategy;
import org.streampipes.model.output.OutputStrategy;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.processors.pattern.detection.flink.config.PatternDetectionFlinkConfig;
import org.streampipes.sdk.StaticProperties;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AbsenceController extends FlinkDataProcessorDeclarer<AbsenceParameters> {

  @Override
  public DataProcessorDescription declareModel() {

    SpDataStream stream1 = new SpDataStream();
    SpDataStream stream2 = new SpDataStream();

    DataProcessorDescription desc = new DataProcessorDescription("absence", "Absence", "Detects whether an event does not arrive within a specified time after the occurrence of another event.");
    desc.setCategory(Arrays.asList(DataProcessorType.PATTERN_DETECT.name()));

    desc.addEventStream(stream1);
    desc.addEventStream(stream2);

    List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
    strategies.add(new CustomOutputStrategy(false));
    desc.setOutputStrategies(strategies);

    List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();

    staticProperties.add(StaticProperties.integerFreeTextProperty("timeWindow", "Time Window Size", "Time window size (seconds)"));
    desc.setStaticProperties(staticProperties);
    desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
    return desc;
  }

  @Override
  public FlinkDataProcessorRuntime<AbsenceParameters> getRuntime(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {
    List<String> selectProperties = new ArrayList<>();
    for (EventProperty p : graph.getOutputStream().getEventSchema().getEventProperties()) {
      selectProperties.add(p.getRuntimeName());
    }

    Integer timeWindowSize = extractor.singleValueParameter("timeWindow", Integer.class);

    AbsenceParameters params = new AbsenceParameters(graph, selectProperties, timeWindowSize);

    return new AbsenceProgram(params, PatternDetectionFlinkConfig.INSTANCE.getDebug());
  }
}
