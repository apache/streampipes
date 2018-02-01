/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.pe.mixed.kafka.wordcount;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.streampipes.wrapper.kafka.KafkaStreamsDataProcessorRuntime;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class WordCountProgram extends KafkaStreamsDataProcessorRuntime<WordCountParameters> {

  public WordCountProgram(WordCountParameters params) {
    super(params);
  }

  @Override
  protected KStream<String, Map<String, Object>> getApplicationLogic(KStream<String, Map<String, Object>>...
                                                                            inputStreams) {
    return inputStreams[0]
            .flatMapValues(value -> Arrays.asList(String.valueOf(value.get(params.getWordFieldName()))))
            .groupBy((key, word) -> word)
            .count()
            .toStream()
            .flatMap(new KeyValueMapper<String, Long, Iterable<KeyValue<String, Map<String, Object>>>>() {
              @Override
              public Iterable<KeyValue<String, Map<String, Object>>> apply(String s, Long aLong) {
                Map<String, Object> outMap = new HashMap<>();
                outMap.put("word", s);
                outMap.put("count", aLong);
                return Arrays.asList(new KeyValue<>(s, outMap));
              }
            });
  }
}
