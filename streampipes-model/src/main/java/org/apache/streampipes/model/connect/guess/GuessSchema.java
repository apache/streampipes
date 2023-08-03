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

package org.apache.streampipes.model.connect.guess;

import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.shared.annotation.TsModel;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
@TsModel
public class GuessSchema {

  public EventSchema eventSchema;

  //public List<Map<String, GuessTypeInfo>> eventPreview;

  private List<String> eventPreview;

  public Map<String, FieldStatusInfo> fieldStatusInfo;

  public GuessSchema() {
    super();
    this.eventPreview = new ArrayList<>();
    this.fieldStatusInfo = new HashMap<>();
  }

  public GuessSchema(GuessSchema other) {
    this.eventSchema = other.getEventSchema() != null ? new EventSchema(other.getEventSchema()) : null;
    this.eventPreview = other.getEventPreview();
    this.fieldStatusInfo = other.getFieldStatusInfo();
  }

  public EventSchema getEventSchema() {
    return eventSchema;
  }

  public void setEventSchema(EventSchema eventSchema) {
    this.eventSchema = eventSchema;
  }

  public List<String> getEventPreview() {
    return eventPreview;
  }

  public void setEventPreview(List<String> eventPreview) {
    this.eventPreview = eventPreview;
  }

  public Map<String, FieldStatusInfo> getFieldStatusInfo() {
    return fieldStatusInfo;
  }

  public void setFieldStatusInfo(Map<String, FieldStatusInfo> fieldStatusInfo) {
    this.fieldStatusInfo = fieldStatusInfo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GuessSchema that = (GuessSchema) o;
    return Objects.equals(eventSchema, that.eventSchema) && Objects.equals(eventPreview,
        that.eventPreview) && Objects.equals(fieldStatusInfo, that.fieldStatusInfo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(eventSchema, eventPreview, fieldStatusInfo);
  }

  @Override
  public String toString() {
    return "GuessSchema{"
           + "eventSchema=" + eventSchema
           + ", eventPreview=" + eventPreview
           + ", fieldStatusInfo=" + fieldStatusInfo
           + '}';
  }
}
