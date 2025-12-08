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

import org.apache.streampipes.commons.prometheus.StreamPipesCollectorRegistry;

import io.prometheus.client.Gauge;


public class PipelinesMetrics {
  @Deprecated
  public static final Gauge ALL_PIPELINES_GAUGE_LEGACY = StreamPipesCollectorRegistry.registerGauge(
                                                   "all_pipelines",
                                                    "Total number of pipelines");

    
  @Deprecated
  public static final Gauge RUNNING_PIPELINES_GAUGE_LEGACY  = StreamPipesCollectorRegistry.registerGauge(
                                                   "running_pipelines",
                                                    "Number of running pipelines");
  @Deprecated
  public static final Gauge STOPPED_PIPELINES_GAUGE_LEGACY  = StreamPipesCollectorRegistry.registerGauge(
                                                    "stopped_pipelines",
                                                     "Number of stopped pipelines");

  @Deprecated
  public static final Gauge HEALTHY_PIPELINES_GAUGE_LEGACY  = StreamPipesCollectorRegistry.registerGauge(
                                                    "healthy_pipelines",
                                                     "Number of healthy pipelines");
  @Deprecated
  public static final Gauge FAILED_PIPELINES_GAUGE_LEGACY  = StreamPipesCollectorRegistry.registerGauge(
                                                    "failed_pipelines",
                                                     "Number of failed pipelines");
  @Deprecated
  public static final Gauge ATTENTION_REQUIRED_PIPELINES_GAUGE_LEGACY  = StreamPipesCollectorRegistry.registerGauge(
                                                    "attention_required_pipelines",
                                                     "Number of pipelines requiring attention");
  @Deprecated
  public static final Gauge ELEMENT_COUNT_GAUGE_LEGACY  = StreamPipesCollectorRegistry.registerGauge(
                                                    "element_count",
                                                     "Total number of elements in the pipeline");

  public static final Gauge ALL_PIPELINES_GAUGE = StreamPipesCollectorRegistry.registerGauge(
                                                   "sp_core_pipeline_count_total",
                                                    "Total number of pipelines");
  public static final Gauge HEALTH_PIPELINES_GAUGE = StreamPipesCollectorRegistry.registerGauge(
                                                   "sp_core_pipeline_health_state",
                                                    "pipelines per status (failed, attention, healthy)",
                                                 "pipelineId", "pipelineName", "operation");
  public static final Gauge STATUS_PIPELINES_GAUGE = StreamPipesCollectorRegistry.registerGauge(
                                                    "sp_core_pipeline_running_state",
                                                     "Number of failed pipelines","pipelineId", "pipelineName", "operation");

  public static final Gauge ELEMENT_COUNT_GAUGE = StreamPipesCollectorRegistry.registerGauge(
                                                    "sp_core_pipeline_element_data_total",
                                                     "Total amount of data received/sent by a pipeline element (e.g., filter)","pipelineId", "elemenetId", "operation");
  
  public static void updatePipelineRunningState(String pipelineId, String pipelineName, boolean state) {

      STATUS_PIPELINES_GAUGE.labels(pipelineId,  pipelineName, state ? "running" : "stopped")
                                       .set(1);
      STATUS_PIPELINES_GAUGE.labels(pipelineId,  pipelineName,!state ? "running" : "stopped")
                                       .set(0);
  }

  public static void updatePipelineHealthState(String pipelineId, String pipelineName, String state) {

    String[] statusElements = {"OK", "FAILURE", "REQUIRES_ATTENTION"};

    for (String s : statusElements) {
        if (s.equals(state)) {
            HEALTH_PIPELINES_GAUGE.labels(pipelineId, pipelineName, s).set(1);
        } else {
            HEALTH_PIPELINES_GAUGE.labels(pipelineId, pipelineName, s).set(0);
        }
    }
  }

}
