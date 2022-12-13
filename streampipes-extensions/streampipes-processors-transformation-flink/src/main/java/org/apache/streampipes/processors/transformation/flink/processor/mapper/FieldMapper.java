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
package org.apache.streampipes.processors.transformation.flink.processor.mapper;

import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.processors.transformation.flink.processor.hasher.algorithm.HashAlgorithmType;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import java.util.List;

public class FieldMapper implements FlatMapFunction<Event, Event> {

  private List<String> replacePropertyNames;
  private String newFieldName;

  public FieldMapper(List<String> replacePropertyNames, String newFieldName) {
    this.replacePropertyNames = replacePropertyNames;
    this.newFieldName = newFieldName;
  }

  @Override
  public void flatMap(Event in, Collector<Event> out) {
    Event event = new Event();
    StringBuilder hashValue = new StringBuilder();

    for (String key : in.getFields().keySet()) {
      if (replacePropertyNames.stream().noneMatch(r -> r.equals(key))) {
        event.addField(in.getFieldBySelector(key));
      } else {
        hashValue.append(in.getFieldBySelector((key)).getAsPrimitive().getAsString());
      }
    }

    event.addField(newFieldName, HashAlgorithmType.MD5.hashAlgorithm().toHashValue(hashValue
        .toString
            ()));
    out.collect(event);
  }
}
