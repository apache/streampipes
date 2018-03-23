/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.streampipes.performance;

import org.streampipes.performance.model.PerformanceTestSettings;
import org.streampipes.performance.util.ParameterTool;

public class PipelinePerformanceTest {

  /**
   * configuration parameters:
   * - number of timestamp enrichment epas
   * - parallelism of enrichment epas
   * - parallelism of elastic epas
   * - total number of events to be simulated
   * - wait time between events in ms
   * - number of producer threads
   * - backend URL (optional)
   */

  public static void main(String[] args) {
    if (args.length < 6) {
      System.out.println("Usage: java -jar streampipes-performance-tests.jar [NUMBER OF TIMESTAMP ENRICH EPAS] " +
              "[PARALELLISM OF ENRICHMENT EPAS], [PARALELLISM OF ELASTIC EPAs], [TOTAL NUMBER OF EVENTS], [WAIT TIME " +
              "BETWEEN EVENTS], [NUMBER OF PRODUCER THREADS]");
    } else {
      PerformanceTestSettings settings = ParameterTool.fromArgs(args);


    }

  }

  // SOURCE

  // EPA

  // SINK
  //Kafka Publisher




}
