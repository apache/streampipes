package org.streampipes.pe.sinks.standalone.samples.dashboard;

import org.streampipes.model.impl.EventSchema;

public class DashboardModel {

  private String pipelineId;
  private EventSchema schema;
  private String broker;

  public static DashboardModel from(DashboardParameters params) {
    DashboardModel model = new DashboardModel();
    model.setPipelineId(params.getPipelineId());
    model.setSchema(params.getSchema());
    model.setBroker(params.getBroker());

    return model;
  }

  public String getPipelineId() {
    return pipelineId;
  }

  public void setPipelineId(String pipelineId) {
    this.pipelineId = pipelineId;
  }

  public EventSchema getSchema() {
    return schema;
  }

  public void setSchema(EventSchema schema) {
    this.schema = schema;
  }

  public String getBroker() {
    return broker;
  }

  public void setBroker(String broker) {
    this.broker = broker;
  }
}
