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
package org.apache.streampipes.commons.prometheus.pipelines;

public class PipelineFlowStats {

  private long receivedTotalData;

  private long pipelineProcessedData;

  private long elementInputTotalData;

  private long elementOutputTotalData;

  public PipelineFlowStats() {
  }

  public long getReceivedTotalData() {
    return receivedTotalData;
  }

  public void setReceivedTotalData(long receivedTotalData) {
    this.receivedTotalData = receivedTotalData;
  }

  public void increaseReceivedTotalData(long num) {
    this.receivedTotalData += num;
  }

  public void increaseReceivedTotalData() {
    this.receivedTotalData += 1;
  }

  public long getPipelineProcessedData() {
    return pipelineProcessedData;
  }

  public void setPipelineProcessedData(long pipelineProcessedData) {
    this.pipelineProcessedData = pipelineProcessedData;
  }

  public void increasePipelineProcessedData(long num) {
    this.pipelineProcessedData += num;
  }

  public void increasePipelineProcessedData() {
    this.pipelineProcessedData += 1;
  }

  public long getElementInputTotalData() {
    return elementInputTotalData;
  }

  public void setElementInputTotalData(long elementInputTotalData) {
    this.elementInputTotalData = elementInputTotalData;
  }

  public void increaseElementInputTotalData(long num) {
    this.elementInputTotalData += num;
  }

  public void increaseElementInputTotalData() {
    this.elementInputTotalData += 1;
  }

  public long getElementOutputTotalData() {
    return elementOutputTotalData;
  }

  public void setElementOutputTotalData(long elementOutputTotalData) {
    this.elementOutputTotalData = elementOutputTotalData;
  }

  public void increaseElementOutputTotalData(long num) {
    this.elementOutputTotalData += num;
  }

  public void increaseElementOutputTotalData() {
    this.elementOutputTotalData += 1;
  }

  public void clear() {
    this.receivedTotalData = 0;
    this.pipelineProcessedData = 0;
    this.elementInputTotalData = 0;
    this.elementOutputTotalData = 0;
  }

  public void metrics() {
    PipelineFlowMetrics.RECEIVED_TOTAL_DATA_GAUGE.set(receivedTotalData);
    PipelineFlowMetrics.PROCESSED_DATA_GAUGE.set(pipelineProcessedData);
    PipelineFlowMetrics.ELEMENT_INPUT_TOTAL_DATA_GAUGE.set(elementInputTotalData);
    PipelineFlowMetrics.ELEMENT_OUTPUT_TOTAL_DATA_GAUGE.set(elementOutputTotalData);

  }

}
