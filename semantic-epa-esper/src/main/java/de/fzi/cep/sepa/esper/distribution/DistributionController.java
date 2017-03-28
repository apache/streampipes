package de.fzi.cep.sepa.esper.distribution;

import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyList;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.runtime.flat.declarer.FlatEpDeclarer;
import de.fzi.cep.sepa.sdk.builder.ProcessingElementBuilder;
import de.fzi.cep.sepa.sdk.helpers.EpProperties;
import de.fzi.cep.sepa.sdk.helpers.EpRequirements;
import de.fzi.cep.sepa.sdk.helpers.OutputStrategies;
import de.fzi.cep.sepa.sdk.helpers.SupportedFormats;
import de.fzi.cep.sepa.sdk.helpers.SupportedProtocols;

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
