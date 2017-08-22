package org.streampipes.sdk.helpers;

/**
 * Created by riemer on 20.03.2017.
 */
public class Labels {

  public static Label from(String internalId, String label, String description) {
    return new Label(internalId, label, description);
  }

}
