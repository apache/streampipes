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
package org.apache.streampipes.performance.model;

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
public class PerformanceTestSettings {

  private Integer numberOfTimestampEnrichmentEpas;
  private Integer parallelismOfEnrichmentEpas;
  private Integer parallelismOfElasticEpas;
  private Long totalNumberofEvents;
  private Long waitTimeBetweenEventsInMs;
  private Integer numProducerThreads;
  private String elasticUrl;

  public PerformanceTestSettings(Integer numberOfTimestampEnrichmentEpas, Integer parallelismOfEnrichmentEpas,
                                 Integer parallelismOfElasticEpas, Long totalNumberofEvents,
                                 Long waitTimeBetweenEventsInMs, Integer numProducerThreads, String elasticUrl) {
    this.numberOfTimestampEnrichmentEpas = numberOfTimestampEnrichmentEpas;
    this.parallelismOfEnrichmentEpas = parallelismOfEnrichmentEpas;
    this.parallelismOfElasticEpas = parallelismOfElasticEpas;
    this.totalNumberofEvents = totalNumberofEvents;
    this.waitTimeBetweenEventsInMs = waitTimeBetweenEventsInMs;
    this.numProducerThreads = numProducerThreads;
    this.elasticUrl = elasticUrl;
  }

  public Integer getNumberOfTimestampEnrichmentEpas() {
    return numberOfTimestampEnrichmentEpas;
  }

  public Integer getParallelismOfEnrichmentEpas() {
    return parallelismOfEnrichmentEpas;
  }

  public Integer getParallelismOfElasticEpas() {
    return parallelismOfElasticEpas;
  }

  public Long getTotalNumberofEvents() {
    return totalNumberofEvents;
  }

  public Long getWaitTimeBetweenEventsInMs() {
    return waitTimeBetweenEventsInMs;
  }

  public Integer getNumProducerThreads() {
    return numProducerThreads;
  }

  public String getElasticUrl() {
    return elasticUrl;
  }
}
