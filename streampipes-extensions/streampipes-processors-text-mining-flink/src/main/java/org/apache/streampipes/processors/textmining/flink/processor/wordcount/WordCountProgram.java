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

package org.apache.streampipes.processors.textmining.flink.processor.wordcount;

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.container.config.ConfigExtractor;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.processors.textmining.flink.AbstractTextMiningProgram;

import org.apache.flink.streaming.api.datastream.DataStream;

import java.io.Serializable;

public class WordCountProgram extends AbstractTextMiningProgram<WordCountParameters> implements Serializable {

  public WordCountProgram(WordCountParameters params,
                          ConfigExtractor configExtractor,
                          StreamPipesClient streamPipesClient) {
    super(params, configExtractor, streamPipesClient);
  }

  @Override
  protected DataStream<Event> getApplicationLogic(
      DataStream<Event>... messageStream) {

    return messageStream[0]
        .flatMap(new WordSplitter(bindingParams.getWordCountFieldName()))
        .keyBy("word")
        .sum("count")
        .flatMap(new WordToEventConverter());
  }

}
