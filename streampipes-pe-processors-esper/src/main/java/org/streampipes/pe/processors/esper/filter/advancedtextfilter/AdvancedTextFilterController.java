package org.streampipes.pe.processors.esper.filter.advancedtextfilter;

import com.google.common.io.Resources;
import org.streampipes.commons.exceptions.SepaParseException;
import org.streampipes.container.util.DeclarerUtils;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.staticproperty.CollectionStaticProperty;
import org.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarerSingleton;

import java.util.List;
import java.util.stream.Collectors;

public class AdvancedTextFilterController extends StandaloneEventProcessorDeclarerSingleton<AdvancedTextFilterParameters> {

  @Override
  public DataProcessorDescription declareModel() {

    try {
      return DeclarerUtils.descriptionFromResources(Resources.getResource("advancedtextfilter.jsonld"), DataProcessorDescription.class);
    } catch (SepaParseException e) {
      e.printStackTrace();
      return null;
    }

  }

  @Override
  public ConfiguredEventProcessor<AdvancedTextFilterParameters>
  onInvocation(DataProcessorInvocation sepa, ProcessingElementParameterExtractor extractor) {
    String operation = SepaUtils.getOneOfProperty(sepa, "operatoin");
    CollectionStaticProperty collection = SepaUtils.getStaticPropertyByInternalName(sepa, "collection", CollectionStaticProperty.class);
    String propertyName = SepaUtils.getMappingPropertyName(sepa, "text-mapping");

    List<String> keywords = collection.getMembers()
            .stream()
            .map(m -> ((FreeTextStaticProperty) m).getValue())
            .collect(Collectors.toList());

    AdvancedTextFilterParameters staticParam = new AdvancedTextFilterParameters(sepa, operation, propertyName, keywords);

    return new ConfiguredEventProcessor<>(staticParam, AdvancedTextFilter::new);
  }
}
