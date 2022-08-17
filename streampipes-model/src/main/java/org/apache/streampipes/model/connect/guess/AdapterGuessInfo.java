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

import java.util.List;
import java.util.Map;

public class AdapterGuessInfo {

  private EventSchema eventSchema;
  private List<Map<String, GuessTypeInfo>> eventPreview;

  public AdapterGuessInfo() {
  }

  public AdapterGuessInfo(EventSchema eventSchema,
                          Map<String, GuessTypeInfo> singleEventSample) {
    this.eventSchema = eventSchema;
    this.eventPreview = List.of(singleEventSample);
  }

  public EventSchema getEventSchema() {
    return eventSchema;
  }

  public void setEventSchema(EventSchema eventSchema) {
    this.eventSchema = eventSchema;
  }

  public List<Map<String, GuessTypeInfo>> getEventPreview() {
    return eventPreview;
  }

  public void setEventPreview(List<Map<String, GuessTypeInfo>> eventPreview) {
    this.eventPreview = eventPreview;
  }
}
