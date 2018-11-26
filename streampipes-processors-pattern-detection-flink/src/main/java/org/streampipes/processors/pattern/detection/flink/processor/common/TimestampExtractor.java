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
package org.streampipes.processors.pattern.detection.flink.processor.common;

import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;

import java.util.Map;

public class TimestampExtractor extends AscendingTimestampExtractor<Map<String, Object>> {

  private String timestampField;

  public TimestampExtractor(String timestampField) {
    this.timestampField = timestampField;
  }

  @Override
  public long extractAscendingTimestamp(Map<String, Object> in) {
    return Long.parseLong(String.valueOf(in.get(timestampField)));
  }
}
