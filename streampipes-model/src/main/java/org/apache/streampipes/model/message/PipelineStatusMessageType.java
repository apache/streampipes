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

package org.apache.streampipes.model.message;

public enum PipelineStatusMessageType {

  PIPELINE_STARTED("Pipeline started", "Pipeline successfully started."),
  PIPELINE_STOPPED("Pipeline stopped", "Pipeline successfully stopped."),

  PIPELINE_NO_DATA("No data arriving", "The input stream did not produce any data."),
  PIPELINE_EXCHANGE_SUCCESS("Stream exchanged", "The input stream was replaced with the backup sensor."),
  PIPELINE_EXCHANGE_FAILURE("Could not exchange stream", "We could not find any backup sensor.");

  private String title;
  private String description;

  PipelineStatusMessageType(String title, String description) {
    this.title = title;
    this.description = description;
  }

  public String title() {
    return title;
  }

  public String description() {
    return description;
  }

}
