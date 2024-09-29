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
package org.apache.streampipes.dataexplorer.api;

import java.util.Map;

/**
 * Interface for counting the number of events per measurement within the StreamPipes data storage.
 */
public interface IDataLakeMeasurementCounter {

  /**
   * Counts the sizes of measurements within the StreamPipes data storage.
   *
   * @return A map where each key represents a measurement name and its corresponding value represents the number of
   *         events contained by that measurement.
   */
  Map<String, Integer> countMeasurementSizes();
}