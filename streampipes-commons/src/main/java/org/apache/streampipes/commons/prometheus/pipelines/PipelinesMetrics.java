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

  public static final Gauge ALL_PIPELINES_GAUGE = StreamPipesCollectorRegistry.registerGauge(
                                                   "all_pipelines",
                                                    "Total number of pipelines");


  public static final Gauge RUNNING_PIPELINES_GAUGE = StreamPipesCollectorRegistry.registerGauge(
                                                   "running_pipelines",
                                                    "Number of running pipelines");

  public static final Gauge STOPPED_PIPELINES_GAUGE = StreamPipesCollectorRegistry.registerGauge(
                                                    "stopped_pipelines",
                                                     "Number of stopped pipelines");


  public static final Gauge HEALTHY_PIPELINES_GAUGE = StreamPipesCollectorRegistry.registerGauge(
                                                    "healthy_pipelines",
                                                     "Number of healthy pipelines");

  public static final Gauge FAILED_PIPELINES_GAUGE = StreamPipesCollectorRegistry.registerGauge(
                                                    "failed_pipelines",
                                                     "Number of failed pipelines");

  public static final Gauge ATTENTION_REQUIRED_PIPELINES_GAUGE = StreamPipesCollectorRegistry.registerGauge(
                                                    "attention_required_pipelines",
                                                     "Number of pipelines requiring attention");

  public static final Gauge ELEMENT_COUNT_GAUGE = StreamPipesCollectorRegistry.registerGauge(
                                                    "element_count",
                                                     "Total number of elements in the pipeline");

}
