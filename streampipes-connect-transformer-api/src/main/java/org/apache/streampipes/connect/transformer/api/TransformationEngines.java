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

package org.apache.streampipes.connect.transformer.api;

import org.apache.streampipes.model.connect.ScriptMetadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public enum TransformationEngines {

  INSTANCE;

  private final Map<String, Supplier<TransformationEngine>> transformationEngines = new HashMap<>();

  public void registerEngine(Supplier<TransformationEngine> engineSupplier) {
    transformationEngines.put(engineSupplier.get().metadata().language(), engineSupplier);
  }

  public TransformationEngine getTransformationEngine(String language) {
    validateSupportedLanguage(language);
    return transformationEngines.get(language).get();
  }

  public void validateSupportedLanguage(String language) {
    if (!transformationEngines.containsKey(language)) {
      throw new IllegalArgumentException(unsupportedLanguageMessage(language));
    }
  }

  public List<ScriptMetadata> getAvailableEngineMetadata() {
    return transformationEngines
        .values()
        .stream()
        .map(transformationEngineSupplier -> transformationEngineSupplier.get().metadata())
        .toList();
  }

  private String unsupportedLanguageMessage(String language) {
    var requestedLanguage = language == null || language.isBlank()
        ? "missing"
        : "'" + language + "'";

    var supportedLanguages = transformationEngines.keySet()
        .stream()
        .sorted()
        .collect(Collectors.joining(", "));

    var message = "Unsupported script transformation language " + requestedLanguage + ". ";
    if (supportedLanguages.isBlank()) {
      return message + "No script transformation languages are available.";
    }

    return message + "Supported languages: " + supportedLanguages + ".";
  }
}
