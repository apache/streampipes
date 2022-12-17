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

package org.apache.streampipes.manager.util;

import org.apache.streampipes.commons.exceptions.NoSepaInPipelineException;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.pipeline.Pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PipelineVerificationUtils {

  /**
   * returns the root node of a partial pipeline (a pipeline without an action)
   *
   * @param pipeline
   * @return {@link org.apache.streampipes.model.base.InvocableStreamPipesEntity}
   */

  public static InvocableStreamPipesEntity getRootNode(Pipeline pipeline) throws NoSepaInPipelineException {
    List<InvocableStreamPipesEntity> elements = new ArrayList<>();
    elements.addAll(pipeline.getSepas());
    elements.addAll(pipeline.getActions());

    List<InvocableStreamPipesEntity> unconfiguredElements = elements
        .stream()
        .filter(e -> !e.isConfigured())
        .collect(Collectors.toList());


    if (unconfiguredElements.size() != 1) {
      throw new NoSepaInPipelineException();
    } else {
      return unconfiguredElements.get(0);
    }

  }
}
