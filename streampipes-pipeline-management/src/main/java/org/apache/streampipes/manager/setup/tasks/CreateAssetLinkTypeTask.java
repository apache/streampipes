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

package org.apache.streampipes.manager.setup.tasks;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.commons.random.UUIDGenerator;
import org.apache.streampipes.model.assets.AssetLinkType;
import org.apache.streampipes.storage.api.IGenericStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CreateAssetLinkTypeTask implements InstallationTask {

  private List<AssetLinkType> defaultLinkTypes = Arrays.asList(
      new AssetLinkType("data-view", "Data View", "var(--color-data-view)", "search", "data-view",
          List.of("dataexplorer"), true),
      new AssetLinkType("dashboard", "Dashboard", "var(--color-dashboard)", "insert_chart", "dashboard",
          List.of("dashboard"), true),
      new AssetLinkType("adapter", "Adapter", "var(--color-adapter)", "power", "adapter", List.of("connect"), true),
      new AssetLinkType("data-source", "Data Source", "var(--color-data-source)", "dataset", "data-source", List.of(),
          false),
      new AssetLinkType("pipeline", "Pipeline", "var(--color-pipeline)", "play_arrow", "pipeline",
          List.of("pipeline", "details"), true),
      new AssetLinkType("measurement", "Data Lake Storage", "var(--color-measurement)", "folder", "measurement",
          List.of(), false),
      new AssetLinkType("file", "File", "var(--color-file)", "draft", "file", List.of(), false)
  );

  @Override
  public void execute() {
    var genericStorage = getGenericStorage();

    this.defaultLinkTypes.forEach(link -> {
      try {
        link.setId(UUIDGenerator.generateUuid());
        genericStorage.create(link, AssetLinkType.class);
      } catch (IOException e) {
        e.printStackTrace();
        throw new SpRuntimeException("Could not create asset link document");
      }
    });
  }

  private IGenericStorage getGenericStorage() {
    return StorageDispatcher.INSTANCE.getNoSqlStore().getGenericStorage();
  }
}
