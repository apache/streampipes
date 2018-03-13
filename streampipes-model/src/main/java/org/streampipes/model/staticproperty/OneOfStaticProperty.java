package org.streampipes.model.staticproperty;

import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@RdfsClass(StreamPipes.ONE_OF_STATIC_PROPERTY)
@Entity
public class OneOfStaticProperty extends SelectionStaticProperty {

  private static final long serialVersionUID = 3483290363677184344L;

  public OneOfStaticProperty() {
    super(StaticPropertyType.OneOfStaticProperty);
  }

  public OneOfStaticProperty(OneOfStaticProperty other) {
    super(other);
  }

  public OneOfStaticProperty(String internalName, String label, String description) {
    super(StaticPropertyType.OneOfStaticProperty, internalName, label, description);
  }


}
