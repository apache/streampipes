package org.streampipes.sdk.builder;

import org.streampipes.commons.Utils;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.sdk.utils.Datatypes;

public class SimpleEpRequirementsBuilder {

  EventPropertyPrimitive eventProperty;

  private SimpleEpRequirementsBuilder() {
    this.eventProperty = new EventPropertyPrimitive();
  }

  public SimpleEpRequirementsBuilder datatypeReq(Datatypes datatype) {
    this.eventProperty.setRuntimeType(datatype.toString());
    return this;
  }

  public SimpleEpRequirementsBuilder domainPropertyReq(String domainProperty) {
    this.eventProperty.setDomainProperties(Utils.createURI(domainProperty));
    return this;
  }

  public EventPropertyPrimitive build() {
    return this.eventProperty;
  }
}
