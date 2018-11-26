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
package org.streampipes.processors.textmining.flink.processor.language;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.streampipes.processors.textmining.flink.AbstractTextMiningProgram;

import java.util.Map;

public class LanguageDetectionProgram extends AbstractTextMiningProgram<LanguageDetectionParameters> {

  public LanguageDetectionProgram(LanguageDetectionParameters params, boolean debug) {
    super(params, debug);
  }

  public LanguageDetectionProgram(LanguageDetectionParameters params) {
    super(params);
  }

  @Override
  protected DataStream<Map<String, Object>> getApplicationLogic(DataStream<Map<String, Object>>... messageStream) {
    return messageStream[0]
            .flatMap(new LanguageDetection(params.getFieldName()));
  }
}
