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

package org.apache.streampipes.processors.transformation.flink.processor.hasher;

import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.processors.transformation.flink.processor.hasher.algorithm.HashAlgorithm;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import java.io.Serializable;

public class FieldHasher implements Serializable, FlatMapFunction<Event, Event> {

  private HashAlgorithm hashAlgorithm;
  private String propertyName;

  public FieldHasher(String propertyName, HashAlgorithm hashAlgorithm) {
    this.propertyName = propertyName;
    this.hashAlgorithm = hashAlgorithm;
  }

  @Override
  public void flatMap(Event in, Collector<Event> out) throws Exception {
    in.updateFieldBySelector(propertyName,
        hashAlgorithm.toHashValue(in.getFieldBySelector(propertyName).getAsPrimitive().getAsString()));
    out.collect(in);
  }

}
