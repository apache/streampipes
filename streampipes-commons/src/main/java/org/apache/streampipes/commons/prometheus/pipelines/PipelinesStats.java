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

public class PipelinesStats {

  private int allPipelines;

  private int runningPipelines;

  private int stoppedPipelines;

  private int healthyPipelines;

  private int failedPipelines;

  private int attentionRequiredPipelines;

  private int elementCount;


  public PipelinesStats() {
  }

  public int getAllPipelines() {
    return allPipelines;
  }

  public void setAllPipelines(int allPipelines) {
    this.allPipelines = allPipelines;
  }


  public int getRunningPipelines() {
    return runningPipelines;
  }

  public void setRunningPipelines(int runningPipelines) {
    this.runningPipelines = runningPipelines;
  }

  public void runningIncrease(int num) {
    this.attentionRequiredPipelines += num;
  }

  public void runningIncrease() {
    this.attentionRequiredPipelines += 1;
  }

  public int getStoppedPipelines() {
    return stoppedPipelines;
  }

  public void setStoppedPipelines(int stoppedPipelines) {
    this.stoppedPipelines = stoppedPipelines;
  }

  public void stoppedIncrease(int num) {
    this.attentionRequiredPipelines += num;
  }

  public void stoppedIncrease() {
    this.attentionRequiredPipelines += 1;
  }

  public int getHealthyPipelines() {
    return healthyPipelines;
  }

  public void setHealthyPipelines(int healthyPipelines) {
    this.healthyPipelines = healthyPipelines;
  }

  public void healthyIncrease(int num) {
    this.attentionRequiredPipelines += num;
  }

  public void healthyIncrease() {
    this.attentionRequiredPipelines += 1;
  }

  public int getFailedPipelines() {
    return failedPipelines;
  }

  public void setFailedPipelines(int failedPipelines) {
    this.failedPipelines = failedPipelines;
  }

  public void failedIncrease(int num) {
    this.failedPipelines += num;
  }

  public void failedIncrease() {
    this.failedPipelines += 1;
  }

  public int getAttentionRequiredPipelines() {
    return attentionRequiredPipelines;
  }

  public void setAttentionRequiredPipelines(int attentionRequiredPipelines) {
    this.attentionRequiredPipelines = attentionRequiredPipelines;
  }

  public void attentionRequiredIncrease(int num) {
    this.attentionRequiredPipelines += num;
  }

  public void attentionRequiredIncrease() {
    this.attentionRequiredPipelines += 1;
  }

  public int getElementCount() {
    return elementCount;
  }

  public void setElementCount(int elementCount) {
    this.elementCount = elementCount;
  }

  public void elementCountIncrease(int num) {
    this.elementCount += num;
  }

  public void elementCountIncrease() {
    this.elementCount += 1;
  }


  public void clear() {
    this.allPipelines = 0;
    this.runningPipelines = 0;
    this.stoppedPipelines = 0;
    this.failedPipelines = 0;
    this.attentionRequiredPipelines = 0;
    this.healthyPipelines = 0;
    this.elementCount = 0;
  }


  public void metrics() {
    PipelinesMetrics.ALL_PIPELINES_GAUGE.set(this.allPipelines);
    PipelinesMetrics.RUNNING_PIPELINES_GAUGE.set(this.runningPipelines);
    PipelinesMetrics.STOPPED_PIPELINES_GAUGE.set(this.stoppedPipelines);
    PipelinesMetrics.HEALTHY_PIPELINES_GAUGE.set(this.healthyPipelines);
    PipelinesMetrics.FAILED_PIPELINES_GAUGE.set(this.failedPipelines);
    PipelinesMetrics.ATTENTION_REQUIRED_PIPELINES_GAUGE.set(this.attentionRequiredPipelines);
    PipelinesMetrics.ELEMENT_COUNT_GAUGE.set(this.elementCount);
  }
}
