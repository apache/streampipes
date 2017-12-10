package org.streampipes.sdk.helpers;

/**
 * Created by riemer on 20.03.2017.
 */
public class Labels {

  public static Label from(String internalId, String label, String description) {
    return new Label(internalId, label, description);
  }

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
