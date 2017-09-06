package org.streampipes.sdk.helpers;

import org.streampipes.model.impl.staticproperty.SupportedProperty;

public class OntologyProperties {

  public static SupportedProperty mandatory(String uri) {
    return from(uri, true);
  }

  public static SupportedProperty optional(String uri) {
    return from(uri, false);
  }

  public static SupportedProperty from(String uri, boolean valueRequired) {
    return new SupportedProperty(uri, valueRequired);
  }
}
