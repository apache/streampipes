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
package org.streampipes.pe.mixed.flink.samples.mapper;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.streampipes.pe.mixed.flink.samples.hasher.HashAlgorithmType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FieldMapper implements FlatMapFunction<Map<String, Object>, Map<String, Object>> {

  private List<String> replacePropertyNames;
  private String newFieldName;

  public FieldMapper(List<String> replacePropertyNames, String newFieldName) {
    this.replacePropertyNames = replacePropertyNames;
    this.newFieldName = newFieldName;
  }

  @Override
  public void flatMap(Map<String, Object> in, Collector<Map<String, Object>> out) throws Exception {
    Map<String, Object> outMap = new HashMap<>();
    StringBuilder hashValue = new StringBuilder();

    for (String key : in.keySet()) {
      if (replacePropertyNames.stream().noneMatch(r -> r.equals(key))) {
        outMap.put(key, in.get(key));
      } else {
        hashValue.append(String.valueOf(outMap.get(key)));
      }
    }

    outMap.put(newFieldName, HashAlgorithmType.MD5.hashAlgorithm().toHashValue(hashValue.toString()));
    out.collect(outMap);
  }
}
