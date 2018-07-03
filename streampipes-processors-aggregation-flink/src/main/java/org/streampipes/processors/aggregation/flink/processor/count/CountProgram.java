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
package org.streampipes.processors.aggregation.flink.processor.count;

import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.streampipes.processors.aggregation.flink.AbstractAggregationProgram;

import java.util.Map;

public class CountProgram extends AbstractAggregationProgram<CountParameters> {

  public CountProgram(CountParameters params, boolean debug) {
    super(params, debug);
    setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);
  }

  @Override
  protected DataStream<Map<String, Object>> getApplicationLogic(DataStream<Map<String, Object>>... dataStreams) {
    // TODO implement
    return null;
  }

}
