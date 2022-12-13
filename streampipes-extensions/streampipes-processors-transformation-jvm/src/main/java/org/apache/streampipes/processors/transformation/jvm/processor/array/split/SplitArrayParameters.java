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

package org.apache.streampipes.processors.transformation.jvm.processor.array.split;

import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.wrapper.params.binding.EventProcessorBindingParams;

import java.util.List;

public class SplitArrayParameters extends EventProcessorBindingParams {
  private String arrayField;
  private List<String> keepProperties;

  public SplitArrayParameters(DataProcessorInvocation graph, String arrayField, List<String> keepProperties) {
    super(graph);
    this.arrayField = arrayField;
    this.keepProperties = keepProperties;
  }

  public String getArrayField() {
    return arrayField;
  }

  public List<String> getKeepProperties() {
    return keepProperties;
  }
}