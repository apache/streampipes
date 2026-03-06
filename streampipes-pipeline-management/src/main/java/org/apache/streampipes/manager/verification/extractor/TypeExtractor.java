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

package org.apache.streampipes.manager.verification.extractor;

import org.apache.streampipes.commons.exceptions.SepaParseException;
import org.apache.streampipes.manager.verification.ElementVerifier;
import org.apache.streampipes.manager.verification.TypedElementVerifier;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.api.pipeline.IPipelineElementDescriptionStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceUrlProvider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class TypeExtractor {

  private static final Logger LOG = LoggerFactory.getLogger(TypeExtractor.class);
  private static final String CLASS_FIELD = "@class";

  private final String extensionElementDescription;
  private final IPipelineElementDescriptionStorage storageApi;

  public TypeExtractor(String extensionElementDescription) {
    this(extensionElementDescription, defaultStorageApi());
  }

  public TypeExtractor(
      String extensionElementDescription,
      IPipelineElementDescriptionStorage storageApi
  ) {
    this.extensionElementDescription = extensionElementDescription;
    this.storageApi = storageApi;
  }

  public ElementVerifier<?> getTypeVerifier() throws SepaParseException {
    var jsonClassName = getClassName();
    LOG.info("Detected type {}", jsonClassName);
    return getTypeDef(jsonClassName);
  }

  private String getClassName() throws SepaParseException {
    try {
      ObjectNode jsonNode =
          JacksonSerializer.getObjectMapper().readValue(extensionElementDescription, ObjectNode.class);
      JsonNode classNode = jsonNode.get(CLASS_FIELD);
      if (classNode == null || classNode.isNull()) {
        throw new SepaParseException();
      }
      return classNode.asText();
    } catch (JsonProcessingException e) {
      throw new SepaParseException();
    }
  }

  private ElementVerifier<?> getTypeDef(String jsonClassName) throws SepaParseException {
    return switch (jsonClassName) {
      case "org.apache.streampipes.model.SpDataStream" -> createVerifier(
          SpDataStream.class,
          storageApi::exists,
          storageApi::storeDataStream,
          storageApi::update,
          SpServiceUrlProvider.DATA_STREAM
      );
      case "org.apache.streampipes.model.graph.DataProcessorDescription" -> createVerifier(
          DataProcessorDescription.class,
          storageApi::exists,
          storageApi::storeDataProcessor,
          storageApi::update,
          SpServiceUrlProvider.DATA_PROCESSOR
      );
      case "org.apache.streampipes.model.graph.DataSinkDescription" -> createVerifier(
          DataSinkDescription.class,
          storageApi::exists,
          storageApi::storeDataSink,
          storageApi::update,
          SpServiceUrlProvider.DATA_SINK
      );
      case "org.apache.streampipes.model.connect.adapter.AdapterDescription" -> createVerifier(
          AdapterDescription.class,
          storageApi::exists,
          storageApi::storeAdapterDescription,
          storageApi::update,
          SpServiceUrlProvider.ADAPTER
      );
      default -> throw new SepaParseException();
    };
  }

  private <T extends NamedStreamPipesEntity> ElementVerifier<T> createVerifier(
      Class<T> elementClass,
      Predicate<T> existsChecker,
      Consumer<T> storeOperation,
      Consumer<T> updateOperation,
      SpServiceUrlProvider serviceUrlProvider
  ) {
    return new TypedElementVerifier<>(
        extensionElementDescription,
        elementClass,
        storageApi,
        existsChecker,
        storeOperation,
        updateOperation,
        serviceUrlProvider
    );
  }

  private static IPipelineElementDescriptionStorage defaultStorageApi() {
    return StorageDispatcher
        .INSTANCE
        .getNoSqlStore()
        .getPipelineElementDescriptionStorage();
  }

}
