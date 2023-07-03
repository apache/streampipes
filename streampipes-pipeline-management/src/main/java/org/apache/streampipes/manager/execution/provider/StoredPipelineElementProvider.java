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

package org.apache.streampipes.manager.execution.provider;

import org.apache.streampipes.manager.execution.PipelineExecutionInfo;
import org.apache.streampipes.manager.storage.RunningPipelineElementStorage;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides pipeline elements from the cache (for stop actions)
 */

public class StoredPipelineElementProvider implements PipelineElementProvider {
  @Override
  public List<InvocableStreamPipesEntity> getProcessorsAndSinks(PipelineExecutionInfo executionInfo) {
    if (RunningPipelineElementStorage.runningProcessorsAndSinks.containsKey(executionInfo.getPipelineId())) {
      return RunningPipelineElementStorage.runningProcessorsAndSinks.get(executionInfo.getPipelineId());
    } else {
      return new ArrayList<>();
    }
  }

}
