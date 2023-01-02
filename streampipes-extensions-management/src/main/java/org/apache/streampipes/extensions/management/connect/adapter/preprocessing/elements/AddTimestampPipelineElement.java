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

package org.apache.streampipes.extensions.management.connect.adapter.preprocessing.elements;

import org.apache.streampipes.extensions.api.connect.IAdapterPipelineElement;

import java.util.Map;

public class AddTimestampPipelineElement implements IAdapterPipelineElement {

  private String runtimeKey;

  public AddTimestampPipelineElement(String runtimeKey) {
    this.runtimeKey = runtimeKey;
  }

  @Override
  public Map<String, Object> process(Map<String, Object> event) {
    event.put(runtimeKey, System.currentTimeMillis());
    return event;
  }

}
