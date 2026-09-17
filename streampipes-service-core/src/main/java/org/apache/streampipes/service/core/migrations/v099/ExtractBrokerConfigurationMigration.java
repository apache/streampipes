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

package org.apache.streampipes.service.core.migrations.v099;

import org.apache.streampipes.model.grounding.ResourceGroundingConverter;
import org.apache.streampipes.service.core.migrations.Migration;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ExtractBrokerConfigurationMigration implements Migration {
  private static final Logger LOG = LoggerFactory.getLogger(ExtractBrokerConfigurationMigration.class);
  private static final int PAGE_SIZE = 100;
  private final GroundingMigrationStorage storage;

  public ExtractBrokerConfigurationMigration() {
    this(new GroundingMigrationStorage());
  }

  ExtractBrokerConfigurationMigration(GroundingMigrationStorage storage) {
    this.storage = storage;
  }

  @Override
  public boolean shouldExecute() {
    try {
      return !storage.isCompleted();
    } catch (IOException e) {
      throw new IllegalStateException("Could not read broker grounding migration completion marker", e);
    }
  }

  @Override
  public void executeMigration() {
    try {
      for (var collection : storage.collections()) {
        String after = null;
        while (true) {
          var page = storage.readPage(collection, after, PAGE_SIZE);
          if (page.isEmpty()) {
            break;
          }
          for (var json : page) {
            var document = JsonParser.parseString(json).getAsJsonObject();
            String id = document.get("_id").getAsString();
            after = id;
            if (!id.startsWith("_design/") && convert(document, collection, id)) {
              update(collection, id, document);
              LOG.info("Migrated event grounding in {}/{}", collection, id);
            }
          }
        }
      }
      storage.markCompleted();
    } catch (IOException e) {
      // Startup must not restore workloads after a partial cleanup.
      throw new IllegalStateException("Broker grounding migration failed; resolve the storage failure and restart", e);
    }
  }

  private boolean convert(JsonObject document, String collection, String id) {
    try {
      return ResourceGroundingConverter.convert(document);
    } catch (RuntimeException e) {
      throw new IllegalStateException("Invalid event grounding in " + collection + "/" + id
          + "; resolve the resource before restarting");
    }
  }

  private void update(String collection, String id, JsonObject document) throws IOException {
    for (int attempt = 0; attempt < 3; attempt++) {
      if (storage.update(collection, id, document.toString())) {
        return;
      }
      document = JsonParser.parseString(storage.read(collection, id)).getAsJsonObject();
      if (!convert(document, collection, id)) {
        return;
      }
    }
    throw new IOException("Unresolved revision conflict in " + collection + "/" + id);
  }

  @Override
  public String getDescription() {
    return "Extract internal broker configuration while preserving event topics";
  }
}
