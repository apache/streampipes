package org.streampipes.pe.algorithms.esper.distribution;

import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyList;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.model.vocabulary.SO;
import org.streampipes.runtime.flat.declarer.FlatEpDeclarer;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;

import java.util.Arrays;
import java.util.List;

public class DistributionController extends FlatEpDeclarer<DistributionParameters> {

  @Override
  public SepaDescription declareModel() {

    return ProcessingElementBuilder.create("distribution", "Distribution", "Computes current " +
            "value distribution")
            .stream1PropertyRequirementWithUnaryMapping(EpRequirements.stringReq(), "mapping",
                    "Property Mapping", "")
            .requiredIntegerParameter("batch-window", "Batch Window", "Number of events to keep " +
                    "for calculating the distribution")
            .supportedProtocols(SupportedProtocols.kafka(), SupportedProtocols.jms())
            .supportedFormats(SupportedFormats.jsonFormat(), SupportedFormats.thriftFormat())
            .outputStrategy(OutputStrategies.fixed(makeOutputSchema()))
            .build();
  }

  private List<EventProperty> makeOutputSchema() {
    EventPropertyList listProperty = new EventPropertyList();
    listProperty.setRuntimeName("rows");

    EventProperty key = EpProperties.stringEp("key", SO.Text);
    EventProperty value = EpProperties.integerEp("value", SO.Text);

    listProperty.setEventProperties(Arrays.asList(key, value));

    return Arrays.asList(listProperty);
  }

  @Override
  public Response invokeRuntime(SepaInvocation sepa) {
    int timeWindow = Integer.parseInt(SepaUtils.getFreeTextStaticPropertyValue(sepa,
            "batch-window"));

    String mapping = SepaUtils.getMappingPropertyName(sepa, "mapping");

    DistributionParameters staticParam = new DistributionParameters(
            sepa,
            timeWindow,
            mapping);

    return submit(staticParam, Distribution::new, sepa);

  }

}
