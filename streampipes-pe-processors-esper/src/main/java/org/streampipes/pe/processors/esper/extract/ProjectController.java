package org.streampipes.pe.processors.esper.extract;

import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarerSingleton;

import java.util.ArrayList;
import java.util.List;


public class ProjectController extends StandaloneEventProcessorDeclarerSingleton<ProjectParameter> {

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("project", "Projection", "Outputs a selectable subset of an input event type")
            .category(DataProcessorType.TRANSFORM)
            .requiredPropertyStream1(EpRequirements.anyProperty())
            .outputStrategy(OutputStrategies.custom())
            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.jms(), SupportedProtocols.kafka())
            .build();
  }

  @Override
  public ConfiguredEventProcessor<ProjectParameter>
  onInvocation(DataProcessorInvocation sepa) {
    List<NestedPropertyMapping> projectProperties = new ArrayList<>();

    for (EventProperty p : sepa.getOutputStream().getEventSchema().getEventProperties()) {
      projectProperties.add(new NestedPropertyMapping(p.getRuntimeName(), SepaUtils.getFullPropertyName(p, sepa.getInputStreams().get(0).getEventSchema().getEventProperties(), "", '.')));
    }

    ProjectParameter staticParam = new ProjectParameter(
            sepa,
            projectProperties);

    return new ConfiguredEventProcessor<>(staticParam, Project::new);
  }
}
