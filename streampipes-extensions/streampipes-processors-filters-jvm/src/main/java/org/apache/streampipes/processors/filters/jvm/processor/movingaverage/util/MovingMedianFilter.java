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

package org.apache.streampipes.processors.filters.jvm.processor.movingaverage.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MovingMedianFilter extends MovingFilter {

  public MovingMedianFilter(int n) {
    super(n);
  }

  @Override
  protected double filterMeasurement(List<Double> measurements) {
    List<Double> sorted = new ArrayList<Double>();
    sorted.addAll(measurements);
    Collections.sort(sorted);
    if (sorted.size() % 2 == 0) {
      return (sorted.get(sorted.size() / 2) + sorted.get(sorted.size() / 2 - 1)) / 2;
    }
    return sorted.get(sorted.size() / 2);
  }

}