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
package org.apache.streampipes.manager.preview;

import org.apache.streampipes.model.base.NamedStreamPipesEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public enum ActivePipelinePreviews {

  INSTANCE;

  private final Map<String, List<NamedStreamPipesEntity>> activePreviews;

  ActivePipelinePreviews() {
    this.activePreviews = new ConcurrentHashMap<>();
  }

  public void addActivePreview(String previewId,
                               List<NamedStreamPipesEntity> activePreviews) {
    this.activePreviews.put(previewId, activePreviews);
  }

  /**
   * Removes the preview atomically, so that concurrent stop requests detach its graphs only once.
   *
   * @return the graphs of the removed preview, or empty if the preview was not (or no longer) active
   */
  public Optional<List<NamedStreamPipesEntity>> removePreview(String previewId) {
    return Optional.ofNullable(this.activePreviews.remove(previewId));
  }

  public Optional<List<NamedStreamPipesEntity>> getInvocationGraphs(String previewId) {
    return Optional.ofNullable(this.activePreviews.get(previewId));
  }

  public boolean isActive(String previewId) {
    return this.activePreviews.containsKey(previewId);
  }
}
