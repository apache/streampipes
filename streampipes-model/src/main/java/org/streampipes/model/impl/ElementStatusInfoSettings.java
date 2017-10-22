package org.streampipes.model.impl;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.UnnamedSEPAElement;

import javax.persistence.Entity;

/**
 * Created by riemer on 30.01.2017.
 */
@RdfsClass("sepa:ElementStatusInfoSettings")
@Entity
public class ElementStatusInfoSettings extends UnnamedSEPAElement {

  @RdfProperty("sepa:elementIdentifier")
  private String elementIdentifier;

  @RdfProperty("sepa:kafkaHost")
  private String kafkaHost;

  @RdfProperty("sepa:kafkaPort")
  private int kafkaPort;

  @RdfProperty("sepa:errorTopic")
  private String errorTopic;

  @RdfProperty("sepa:statsTopic")
  private String statsTopic;

  public ElementStatusInfoSettings() {
    super();
  }

  public ElementStatusInfoSettings(ElementStatusInfoSettings other) {
    super(other);
    this.kafkaHost = other.getKafkaHost();
    this.kafkaPort = other.getKafkaPort();
    this.errorTopic = other.getErrorTopic();
    this.statsTopic = other.getStatsTopic();
  }


  public ElementStatusInfoSettings(String elementIdentifier, String kafkaHost, int kafkaPort,
                                   String errorTopic, String
          statsTopic) {
    this.elementIdentifier = elementIdentifier;
    this.kafkaHost = kafkaHost;
    this.kafkaPort = kafkaPort;
    this.errorTopic = errorTopic;
    this.statsTopic = statsTopic;
  }

  public String getKafkaHost() {
    return kafkaHost;
  }

  public void setKafkaHost(String kafkaHost) {
    this.kafkaHost = kafkaHost;
  }

  public int getKafkaPort() {
    return kafkaPort;
  }

  public void setKafkaPort(int kafkaPort) {
    this.kafkaPort = kafkaPort;
  }

  public String getErrorTopic() {
    return errorTopic;
  }

  public void setErrorTopic(String errorTopic) {
    this.errorTopic = errorTopic;
  }

  public String getStatsTopic() {
    return statsTopic;
  }

  public void setStatsTopic(String statsTopic) {
    this.statsTopic = statsTopic;
  }

  public String getElementIdentifier() {
    return elementIdentifier;
  }

  public void setElementIdentifier(String elementIdentifier) {
    this.elementIdentifier = elementIdentifier;
  }
}
