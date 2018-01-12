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
package org.streampipes.pe.mixed.flink.samples.performance;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import java.util.Map;

public class PerformanceTestTimestampEnricher implements FlatMapFunction<Map<String, Object>, Map<String, Object>> {

  private final String timestampFieldName;

  public PerformanceTestTimestampEnricher(String timestampFieldName) {
    this.timestampFieldName = timestampFieldName;
  }


  @Override
  public void flatMap(Map<String, Object> in, Collector<Map<String, Object>> out) throws Exception {
    in.put(timestampFieldName, System.currentTimeMillis());
    out.collect(in);
  }
}
