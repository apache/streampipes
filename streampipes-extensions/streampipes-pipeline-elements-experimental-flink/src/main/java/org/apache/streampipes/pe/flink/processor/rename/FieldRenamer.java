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

package org.apache.streampipes.pe.flink.processor.rename;

import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.field.AbstractField;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

public class FieldRenamer implements FlatMapFunction<Event, Event> {

  private static final long serialVersionUID = 1L;

  private String oldPropertyName;
  private String newPropertyName;

  public FieldRenamer(String oldPropertyName, String newPropertyName) {
    this.oldPropertyName = oldPropertyName;
    this.newPropertyName = newPropertyName;
  }

  @Override
  public void flatMap(Event in,
                      Collector<Event> out) throws Exception {
    AbstractField<?> propertyValue = in.getFieldBySelector(oldPropertyName);
    in.removeFieldBySelector(oldPropertyName);
    in.addField(newPropertyName, propertyValue);
    out.collect(in);
  }

}
