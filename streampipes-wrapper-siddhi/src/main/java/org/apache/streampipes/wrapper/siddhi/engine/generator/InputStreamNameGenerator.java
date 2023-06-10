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
package org.apache.streampipes.wrapper.siddhi.engine.generator;

import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.wrapper.siddhi.utils.SiddhiUtils;

import java.util.ArrayList;
import java.util.List;

public class InputStreamNameGenerator {

  private final IDataProcessorParameters params;

  public InputStreamNameGenerator(IDataProcessorParameters params) {
    this.params = params;
  }

  public List<String> generateInputStreamNames() {
    List<String> inputStreamNames = new ArrayList<>();
    this.params.getInEventTypes().forEach((key, value) -> {
      // TODO why is the prefix not in the parameters.getInEventType
      //registerEventTypeIfNotExists(key, value);
      inputStreamNames.add(SiddhiUtils.prepareName(key));
    });

    return inputStreamNames;
  }
}
