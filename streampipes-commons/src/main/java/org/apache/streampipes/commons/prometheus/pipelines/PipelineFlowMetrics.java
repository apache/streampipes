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


public class PipelineFlowMetrics {

  public static final Gauge RECEIVED_TOTAL_DATA_GAUGE = StreamPipesCollectorRegistry.registerGauge(
      "received_total_data",
      "Total amount of data received by the pipeline"
  );

  public static final Gauge PROCESSED_DATA_GAUGE = StreamPipesCollectorRegistry.registerGauge(
      "processed_data",
      "Number of data obtained from pipeline processing"
  );

  public static final Gauge ELEMENT_INPUT_TOTAL_DATA_GAUGE = StreamPipesCollectorRegistry.registerGauge(
      "element_input_total_data",
      "Total amount of data received by elements"
  );

  public static final Gauge ELEMENT_OUTPUT_TOTAL_DATA_GAUGE = StreamPipesCollectorRegistry.registerGauge(
      "element_output_total_data",
      "Total amount of data sent by elements"
  );

}
