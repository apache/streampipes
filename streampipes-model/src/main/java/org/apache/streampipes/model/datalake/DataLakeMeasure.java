/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.model.datalake;

import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.shared.annotation.TsIgnore;
import org.apache.streampipes.model.shared.annotation.TsModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.gson.annotations.SerializedName;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
@TsModel
public class DataLakeMeasure {

  public static final String CURRENT_SCHEMA_VERSION = "1.1";
  public static final String ASSERTION_ERROR_MESSAGE = "timestamp field requires a stream prefix (e.g. s0::timestamp)";
  private static final String STREAM_PREFIX_DELIMITER = "::";

  @JsonProperty("elementId")
  protected @SerializedName("_id") String elementId;

  @JsonProperty("_rev")
  private @SerializedName("_rev") String rev;

  private String measureName;

  private String timestampField;
  private EventSchema eventSchema;
  private String pipelineId;
  private String pipelineName;
  private boolean pipelineIsRunning;

  private String schemaVersion;

  public DataLakeMeasure() {
    super();
  }

  public DataLakeMeasure(DataLakeMeasure other) {
    this.measureName = other.getMeasureName();
    this.eventSchema = new EventSchema(other.getEventSchema());

  }

  public DataLakeMeasure(String measureName, EventSchema eventSchema) {
    this.measureName = measureName;
    this.eventSchema = eventSchema;
  }

  public DataLakeMeasure(String measureName, String timestampField, EventSchema eventSchema) {
    this.measureName = measureName;
    this.eventSchema = eventSchema;
    this.timestampField = timestampField;
  }

  public String getMeasureName() {
    return measureName;
  }

  public void setMeasureName(String measureName) {
    this.measureName = measureName;
  }

  public EventSchema getEventSchema() {
    return eventSchema;
  }

  public void setEventSchema(EventSchema eventSchema) {
    this.eventSchema = eventSchema;
  }

  public String getPipelineId() {
    return pipelineId;
  }

  public void setPipelineId(String pipelineId) {
    this.pipelineId = pipelineId;
  }

  public String getPipelineName() {
    return pipelineName;
  }

  public void setPipelineName(String pipelineName) {
    this.pipelineName = pipelineName;
  }

  public boolean isPipelineIsRunning() {
    return pipelineIsRunning;
  }

  public void setPipelineIsRunning(boolean pipelineIsRunning) {
    this.pipelineIsRunning = pipelineIsRunning;
  }

  public String getSchemaVersion() {
    return schemaVersion;
  }

  public void setSchemaVersion(String schemaVersion) {
    this.schemaVersion = schemaVersion;
  }

  public String getTimestampField() {
    return timestampField;
  }

  public void setTimestampField(String timestampField) {
    assert timestampField.split(STREAM_PREFIX_DELIMITER).length == 2 : ASSERTION_ERROR_MESSAGE;
    this.timestampField = timestampField;
  }

  /**
   * This can be used to get the name of the timestamp property without the stream prefix
   *
   * @return the name of the timestamp property
   */
  @TsIgnore
  @JsonIgnore
  public String getTimestampFieldName() {
    return timestampField.split(STREAM_PREFIX_DELIMITER)[1];
  }

  public String getRev() {
    return rev;
  }

  public void setRev(String rev) {
    this.rev = rev;
  }

  public String getElementId() {
    return elementId;
  }

  public void setElementId(String elementId) {
    this.elementId = elementId;
  }
}
