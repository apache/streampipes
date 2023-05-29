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

package org.apache.streampipes.extensions.management.init;

import org.apache.streampipes.extensions.api.pe.runtime.IStreamPipesRuntime;
import org.apache.streampipes.extensions.management.util.ElementInfo;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum RunningInstances {
  INSTANCE;

  private final Map<String,
      ElementInfo<NamedStreamPipesEntity, IStreamPipesRuntime<?, ?>>> runningInstances = new HashMap<>();


  public void add(String id,
                  NamedStreamPipesEntity description,
                  IStreamPipesRuntime<?, ?> runtime) {
    runningInstances.put(id, new ElementInfo<>(description, runtime));
  }

  public boolean exists(String runningInstanceId) {
    return runningInstances.containsKey(runningInstanceId);
  }

  public IStreamPipesRuntime<?, ?> getInvocation(String id) {
    ElementInfo<NamedStreamPipesEntity, IStreamPipesRuntime<?, ?>> result = runningInstances.get(id);
    if (result != null) {
      return result.getInvocation();
    } else {
      return null;
    }
  }

  public NamedStreamPipesEntity getDescription(String id) {
    return runningInstances.get(id).getDescription();
  }

  public void remove(String id) {
    runningInstances.remove(id);
  }

  public Integer getRunningInstancesCount() {
    return runningInstances.size();
  }

  public List<String> getRunningInstanceIdsForElement(String appId) {
    // TODO change this to appId for STREAMPIPES-319
    List<String> instanceIds = new ArrayList<>();
    this.runningInstances.forEach((key, elementInfo) -> {
      if (elementInfo.getDescription().getAppId().equals(appId)) {
        instanceIds.add(key);
      }
    });

    return instanceIds;
  }
}
