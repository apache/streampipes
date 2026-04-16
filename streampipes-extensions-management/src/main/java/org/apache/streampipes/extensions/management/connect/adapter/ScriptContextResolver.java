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

package org.apache.streampipes.extensions.management.connect.adapter;

import org.apache.streampipes.client.api.IStreamPipesClient;
import org.apache.streampipes.connect.transformer.api.Context;
import org.apache.streampipes.connect.transformer.groovy.GroovyScriptContext;
import org.apache.streampipes.connect.transformer.js.GraalJsScriptContext;
import org.apache.streampipes.extensions.management.client.StreamPipesClientResolver;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.resource.management.PermissionResourceManager;

public class ScriptContextResolver {

  public Context resolve(AdapterDescription adapterDescription) {
    var userId = getUserId(adapterDescription);
    var language = adapterDescription.getTransformationConfig().getLanguage();
    return resolve(userId, language);
  }

  public Context resolve(String userId, String language) {
    if (userId == null || userId.isBlank()) {
      return null;
    }
    var client = new StreamPipesClientResolver().makeStreamPipesClientInstance().onBehalfOf(userId);

    return createContext(client, language);
  }

  private Context createContext(IStreamPipesClient client, String language) {
    switch (language) {
      case "javascript" -> {
        return new GraalJsScriptContext(client);
      }
      case "groovy" -> {
        return new GroovyScriptContext(client);
      }
      default -> throw new UnsupportedOperationException("Unsupported language: " + language);
    }
  }

  private String getUserId(AdapterDescription adapterDescription) {
    if (!adapterDescription.getTransformationConfig().isScriptActive()) {
      return null;
    }

    if (adapterDescription.getCorrespondingDataStreamElementId() == null
            || adapterDescription.getCorrespondingDataStreamElementId().isBlank()) {
      return null;
    }

    var permissions = new PermissionResourceManager()
            .findForObjectId(adapterDescription.getCorrespondingDataStreamElementId());

    if (permissions.isEmpty()) {
      return null;
    }

    return permissions.get(0).getOwnerSid();
  }
}
