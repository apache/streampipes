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
package org.apache.streampipes.commons.prometheus;


import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.Summary;


public class StreamPipesCollectorRegistry {

  private static final CollectorRegistry collectorRegistry = new CollectorRegistry(true);



  public static CollectorRegistry getCollectorRegistry() {
    return collectorRegistry;
  }

  public static Gauge registerGauge(String name, String help) {

    return Gauge.build()
      .name(name)
      .help(help)
      .register(collectorRegistry);
  }

  public static Counter registerCounter(String name, String help) {
    return Counter.build()
      .name(name)
      .help(help)
      .register(collectorRegistry);
  }

  public static Histogram registerHistogram(String name, String help) {
    return Histogram.build()
      .name(name)
      .help(help)
      .register(collectorRegistry);
  }

  public static Summary registerSummary(String name, String help) {
    return Summary.build()
      .name(name)
      .help(help)
      .register(collectorRegistry);
  }
  public static Gauge registerGauge(String name) {
    return Gauge.build()
      .name(name)
      .register(collectorRegistry);
  }

  public static Counter registerCounter(String name) {
    return Counter.build()
      .name(name)
      .register(collectorRegistry);
  }

  public static Histogram registerHistogram(String name) {
    return Histogram.build()
      .name(name)
      .register(collectorRegistry);
  }

  public static Summary registerSummary(String name) {
    return Summary.build()
      .name(name)
      .register(collectorRegistry);
  }


}
