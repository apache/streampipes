package org.streampipes.sdk.helpers;

public class Labels {

  /**
   * Creates a new label with internalId, label and description. Fully-configured labels are required by static
   * properties and are mandatory for event properties.
   * @param internalId The internal identifier of the element, e.g., "latitude-field-mapping"
   * @param label A human-readable title
   * @param description A human-readable brief summary of the element.
   * @return
   */
  public static Label from(String internalId, String label, String description) {
    return new Label(internalId, label, description);
  }

  /**
   * Creates a new label only with an internal id. Static properties require a fully-specified label, see {@link #from(String, String, String)}
   * @param internalId The internal identifier of the element, e.g., "latitude-field-mapping"
   * @return
   */
  public static Label withId(String internalId) {
    return new Label(internalId, "", "");
  }

  public static Label withTitle(String label, String description) {
    return new Label("", label, description);
  }

  public static Label empty() {
    return new Label("", "", "");
  }

}
