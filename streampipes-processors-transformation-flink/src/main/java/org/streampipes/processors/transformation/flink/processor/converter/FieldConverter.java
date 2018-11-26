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
package org.streampipes.processors.transformation.flink.processor.converter;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.vocabulary.XSD;

import java.util.Map;

public class FieldConverter implements FlatMapFunction<Map<String, Object>, Map<String, Object>> {

  private static Logger LOG = LoggerFactory.getLogger(FieldConverter.class);

  private String convertProperty;
  private String targetDatatype;

  public FieldConverter(String convertProperty, String targetDatatype) {
    this.convertProperty = convertProperty;
    this.targetDatatype = targetDatatype;
  }


  @Override
  public void flatMap(Map<String, Object> in, Collector<Map<String, Object>> out) {
      String value = String.valueOf(in.get(convertProperty));
      try {
          if (targetDatatype.equals(XSD._float.toString())) {
              in.put(convertProperty, Float.parseFloat(value));
          } else {
              in.put(convertProperty, Integer.parseInt(value));
          }
          
          out.collect(in);

      } catch (NumberFormatException e) {
          LOG.error("Field Converter could not convert value: " + value + " of event: " + in);
      }

  }
}
