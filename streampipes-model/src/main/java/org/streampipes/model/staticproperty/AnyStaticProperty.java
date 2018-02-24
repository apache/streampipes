package org.streampipes.model.staticproperty;

import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@RdfsClass(StreamPipes.ANY_STATIC_PROPERTY)
@Entity
public class AnyStaticProperty extends SelectionStaticProperty {

  private static final long serialVersionUID = -7046019539598560494L;

  public AnyStaticProperty() {
    super(StaticPropertyType.AnyStaticProperty);
  }

  public AnyStaticProperty(AnyStaticProperty other) {
    super(other);
  }

  public AnyStaticProperty(String internalName, String label, String description) {
    super(StaticPropertyType.AnyStaticProperty, internalName, label, description);
  }


}
