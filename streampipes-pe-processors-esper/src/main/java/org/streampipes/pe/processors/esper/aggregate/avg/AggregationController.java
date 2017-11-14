package org.streampipes.pe.processors.esper.aggregate.avg;

import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.model.DataProcessorType;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.pe.processors.esper.config.EsperConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.Options;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.wrapper.ConfiguredEventProcessor;
import org.streampipes.wrapper.runtime.EventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarerSingleton;

import java.util.ArrayList;
import java.util.List;

public class AggregationController extends StandaloneEventProcessorDeclarerSingleton<AggregationParameter> {

  @Override
  public DataProcessorDescription declareModel() {

    return ProcessingElementBuilder.create("aggregation", "Aggregation", "Performs different " +
            "aggregation functions")
            .category(DataProcessorType.AGGREGATE)
            .iconUrl(EsperConfig.iconBaseUrl + "/Aggregation_Icon_HQ.png")
            .requiredPropertyStream1WithUnaryMapping(EpRequirements.numberReq(), "aggregate",
                    "Property Selection", "Specifies the event property from your stream that should be aggregated.")
            .naryMappingPropertyWithoutRequirement("groupBy", "Group by", "Partitions the incoming stream by the selected event " +
                    "properties")
            .outputStrategy(OutputStrategies.append(EpProperties.doubleEp(Labels.empty(), "aggregatedValue",
                    "http://schema.org/Number")))
            .requiredIntegerParameter("outputEvery", "Output Frequency", "Output values every " +
                    "(seconds")
            .requiredIntegerParameter("timeWindow", "Time Window Size", "Size of the time window " +
                    "in seconds")
            .requiredSingleValueSelection("operation", "Operation", "Aggregation operation type",
                    Options.from("Average", "Sum", "Min", "Max"))
            .supportedFormats(StandardTransportFormat.standardFormat())
            .supportedProtocols(StandardTransportFormat.standardProtocols())
            .build();
  }


  @Override
  public ConfiguredEventProcessor<AggregationParameter, EventProcessor<AggregationParameter>> onInvocation
          (DataProcessorInvocation sepa) {
    ProcessingElementParameterExtractor extractor = ProcessingElementParameterExtractor.from(sepa);

    List<String> groupBy = SepaUtils.getMultipleMappingPropertyNames(sepa, "groupBy", true);

    String aggregate = SepaUtils.getMappingPropertyName(sepa, "aggregate");

    Integer outputEvery = extractor.singleValueParameter("outputEvery", Integer.class);
    Integer timeWindowSize = extractor.singleValueParameter("timeWindow", Integer.class);
    String aggregateOperation = extractor.selectedSingleValue("operation", String.class);

    List<String> selectProperties = new ArrayList<>();
    for (EventProperty p : sepa.getInputStreams().get(0).getEventSchema().getEventProperties()) {
      selectProperties.add(p.getRuntimeName());
    }

    AggregationParameter staticParam = new AggregationParameter(sepa, convert(aggregateOperation),
            outputEvery, groupBy,
            aggregate, timeWindowSize, selectProperties);

    return new ConfiguredEventProcessor<>(staticParam, Aggregation::new);
  }

  private AggregationType convert(String aggregateOperation) {
    if (aggregateOperation.equals("Average")) {
      return AggregationType.AVG;
    } else if (aggregateOperation.equals("Sum")) {
      return AggregationType.SUM;
    } else if (aggregateOperation.equals("Min")) {
      return AggregationType.MIN;
    } else {
      return AggregationType.MAX;
    }
  }
}