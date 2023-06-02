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
package org.apache.streampipes.pe.flink.processor.converter;

import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.vocabulary.XSD;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldConverter implements FlatMapFunction<Event, Event> {

  private static final Logger LOG = LoggerFactory.getLogger(FieldConverter.class);

  private String convertProperty;
  private String targetDatatype;

  public FieldConverter(String convertProperty, String targetDatatype) {
    this.convertProperty = convertProperty;
    this.targetDatatype = targetDatatype;
  }


  @Override
  public void flatMap(Event in, Collector<Event> out) {
    String value = in.getFieldBySelector(convertProperty).getAsPrimitive().getAsString();
    try {
      if (targetDatatype.equals(XSD.FLOAT.toString())) {
        in.updateFieldBySelector(convertProperty, Float.parseFloat(value.trim()));
      } else if (targetDatatype.equals(XSD.INTEGER.toString())) {
        in.updateFieldBySelector(convertProperty, Integer.parseInt(value.trim()));
      } else if (targetDatatype.equals(XSD.BOOLEAN.toString())) {
        in.updateFieldBySelector(convertProperty, Boolean.parseBoolean(value.trim()));
      } else if (targetDatatype.equals(XSD.STRING.toString())) {
        in.updateFieldBySelector(convertProperty, value.trim());
      }

      out.collect(in);

    } catch (NumberFormatException e) {
      LOG.error("Field Converter could not convert value: " + value + " of event: " + in);
    }

  }
}
