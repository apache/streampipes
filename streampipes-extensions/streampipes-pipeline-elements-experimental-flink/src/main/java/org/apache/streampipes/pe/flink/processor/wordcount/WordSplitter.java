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

package org.apache.streampipes.pe.flink.processor.wordcount;

import org.apache.streampipes.model.runtime.Event;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

public class WordSplitter implements FlatMapFunction<Event, Word> {

  private String mappingPropertyName;

  public WordSplitter(String mappingPropertyName) {
    this.mappingPropertyName = mappingPropertyName;
  }

  @Override
  public void flatMap(Event in,
                      Collector<Word> out) throws Exception {

    String propertyValue = in.getFieldBySelector(mappingPropertyName).getAsPrimitive().getAsString();
    for (String word : propertyValue.split(" ")) {
      out.collect(new Word(word, 1));
    }
  }

}
