/*
 * Copyright 2017 FZI Forschungszentrum Informatik
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
 */

package org.streampipes.processors.pattern.detection.flink.processor.sequence;

import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;

import java.util.Map;

public class SequenceProgram extends FlinkDataProcessorRuntime<SequenceParameters> {

  public SequenceProgram(SequenceParameters params) {
    super(params);
  }

  public SequenceProgram(SequenceParameters params, FlinkDeploymentConfig config) {
    super(params, config);
  }

  @Override
  protected DataStream<Map<String, Object>> getApplicationLogic(DataStream<Map<String, Object>>... dataStreams) {
    return dataStreams[0].keyBy(getKeySelector()).connect(dataStreams[1].keyBy(getKeySelector())).process(new Sequence(params
            .getTimeUnit(),
            params
            .getTimeWindow
            ()));
  }

  private KeySelector<Map<String,Object>, String> getKeySelector() {
    return new KeySelector<Map<String,Object>, String>() {
      @Override
      public String getKey(Map<String, Object> value) throws Exception {
        return "dummy-key";
      }
    };
  }
}
