package de.fzi.cep.sepa.sdk.extractor;

import com.github.drapostolos.typeparser.TypeParser;
import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.OneOfStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.Option;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by riemer on 20.03.2017.
 */
public abstract class AbstractParameterExtractor<T extends InvocableSEPAElement> {

  protected T sepaElement;
  private TypeParser typeParser;

  public AbstractParameterExtractor(T sepaElement) {
    this.sepaElement = sepaElement;
    this.typeParser = TypeParser.newBuilder().build();
  }

  public String inputTopic(Integer streamIndex) {
    return sepaElement
            .getInputStreams()
            .get(streamIndex)
            .getEventGrounding()
            .getTransportProtocol()
            .getTopicName();
  }

  public <V> V singleValueParameter(String internalName, Class<V> targetClass) {
    return typeParser.parse(getStaticPropertyByName(internalName, FreeTextStaticProperty.class)
            .getValue(), targetClass);
  }

  public <V> V selectedSingleValue(String internalName, Class<V> targetClass) {
      return typeParser.parse(getStaticPropertyByName(internalName, OneOfStaticProperty.class)
              .getOptions()
              .stream()
              .filter(Option::isSelected)
              .findFirst()
              .get()
              .getName(), targetClass);

  }

  public <V> List<V> selectedMultiValues(String internalName, Class<V> targetClass) {
    return getStaticPropertyByName(internalName, OneOfStaticProperty.class)
            .getOptions()
            .stream()
            .filter(Option::isSelected)
            .map(o -> o.getName())
            .map(o -> typeParser.parse(o, targetClass))
            .collect(Collectors.toList());
  }

  private <S extends StaticProperty> S getStaticPropertyByName(String internalName, Class<S>
          spType) {
    return spType.cast(getStaticPropertyByName(internalName));
  }

  private StaticProperty getStaticPropertyByName(String name)
  {
    for(StaticProperty p : sepaElement.getStaticProperties())
    {
      if (p.getInternalName().equals(name)) return p;
    }
    return null;
  }
}
