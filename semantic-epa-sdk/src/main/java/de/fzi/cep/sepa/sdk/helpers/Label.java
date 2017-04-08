package de.fzi.cep.sepa.sdk.helpers;

/**
 * Created by riemer on 07.04.2017.
 */
public class Label {

  private String internalId;
  private String label;
  private String description;

  public Label(String internalId, String label, String description) {
    this.internalId = internalId;
    this.label = label;
    this.description = description;
  }

  public String getInternalId() {
    return internalId;
  }

  public void setInternalId(String internalId) {
    this.internalId = internalId;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
