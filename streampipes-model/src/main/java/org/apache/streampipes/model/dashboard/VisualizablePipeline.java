/*
Copyright 2020 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.apache.streampipes.model.dashboard;

import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.vocabulary.StreamPipes;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

@RdfsClass(StreamPipes.VISUALIZABLE_PIPELINE)
public class VisualizablePipeline extends DashboardEntity {

  @RdfProperty(StreamPipes.HAS_PIPELINE_ID)
  private String pipelineId;

  @OneToOne(fetch = FetchType.EAGER,
          cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @RdfProperty(StreamPipes.HAS_SCHEMA)
  private EventSchema schema;

  @RdfProperty(StreamPipes.HAS_VISUALIZATION_NAME)
  private String visualizationName;

  @RdfProperty(StreamPipes.HAS_TOPIC)
  private String topic;

  public VisualizablePipeline() {
    super();
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

  public String getVisualizationName() {
    return visualizationName;
  }

  public void setVisualizationName(String visualizationName) {
    this.visualizationName = visualizationName;
  }

  public String getTopic() {
    return topic;
  }

  public void setTopic(String topic) {
    this.topic = topic;
  }
}
