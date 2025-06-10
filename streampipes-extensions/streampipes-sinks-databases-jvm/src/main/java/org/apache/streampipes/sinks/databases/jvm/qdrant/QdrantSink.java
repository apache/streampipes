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

package org.apache.streampipes.sinks.databases.jvm.qdrant;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.extractor.IDataSinkParameterExtractor;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.vocabulary.XSD;
import org.apache.streampipes.wrapper.params.compat.SinkParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataSink;

import io.qdrant.client.PointIdFactory;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.VectorFactory;
import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Collections.VectorParams;
import io.qdrant.client.grpc.JsonWithInt.Value;
import io.qdrant.client.grpc.Points.NamedVectors;
import io.qdrant.client.grpc.Points.PointStruct;
import io.qdrant.client.grpc.Points.Vectors;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class QdrantSink extends StreamPipesDataSink {
  public static final String QDRANT_HOST_KEY = "qdrant_host";
  public static final String QDRANT_PORT_KEY = "qdrant_port";
  public static final String QDRANT_API_KEY_KEY = "qdrant_api_key";
  public static final String COLLECTION_NAME_KEY = "qdrant_collection_name";
  public static final String VECTOR_NAME_KEY = "qdrant_vector_field";
  public static final String VECTOR_DIMENSION_KEY = "qdrant_vector_dimension";
  public static final String VECTOR_DISTANCE_KEY = "qdrant_distance_metric";
  public static final String ID_KEY = "qdrant_id";

  private QdrantClient client;
  private String vector;
  private String id;
  private String collectionName;
  private Integer dimension;
  private Distance distanceType;

  public static final String BYTE = XSD.BYTE.toString();
  public static final String SHORT = XSD.SHORT.toString();
  public static final String LONG = XSD.LONG.toString();
  public static final String INT = XSD.INT.toString();
  public static final String FLOAT = XSD.FLOAT.toString();
  public static final String DOUBLE = XSD.DOUBLE.toString();
  public static final String BOOLEAN = XSD.BOOLEAN.toString();
  public static final String STRING = XSD.STRING.toString();

  private static final Map<String, Distance> DISTANCE_TYPE_MAP =
      new HashMap<>() {
        {
          put("Cosine", Distance.Cosine);
          put("Euclid", Distance.Euclid);
          put("Dot", Distance.Dot);
          put("Manhattan", Distance.Manhattan);
        }
      };

  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder.create("org.apache.streampipes.sinks.databases.jvm.qdrant", 0)
        .withLocales(Locales.EN)
        .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
        .category(DataSinkType.DATABASE)
        .requiredTextParameter(Labels.withId(QDRANT_HOST_KEY), "localhost")
        .requiredIntegerParameter(Labels.withId(QDRANT_PORT_KEY), 6334)
        .requiredTextParameter(Labels.withId(QDRANT_API_KEY_KEY), "<optional-api-key>")
        .requiredTextParameter(Labels.withId(COLLECTION_NAME_KEY))
        .requiredTextParameter(Labels.withId(ID_KEY))
        .requiredIntegerParameter(Labels.withId(VECTOR_DIMENSION_KEY))
        .requiredStream(
            StreamRequirementsBuilder.create()
                .requiredPropertyWithUnaryMapping(
                    EpRequirements.anyProperty(),
                    Labels.withId(VECTOR_NAME_KEY),
                    PropertyScope.NONE)
                .build())
        .requiredSingleValueSelection(
            Labels.withId(VECTOR_DISTANCE_KEY),
            Options.from(DISTANCE_TYPE_MAP.keySet().toArray(new String[0])))
        .build();
  }

  @Override
  public void onInvocation(SinkParams parameters, EventSinkRuntimeContext runtimeContext)
      throws SpRuntimeException {
    var extractor = parameters.extractor();

    final String host = validateAndExtractHost(extractor);
    final Integer port = validateAndExtractPort(extractor);
    final String apiKey = validateAndExtractApiKey(extractor);
    this.collectionName = validateAndExtractCollectionName(extractor);
    this.id = validateAndExtractId(extractor);
    this.vector = validateAndExtractVectorField(extractor);
    this.dimension = validateAndExtractDimension(extractor);
    this.distanceType = validateAndExtractDistanceType(extractor);

    try {
      client = new QdrantClient(QdrantGrpcClient.newBuilder(host, port).withApiKey(apiKey).build());

      createOrValidateCollection();

    } catch (Exception e) {
      if (client != null) {
        client.close();
      }
      throw new SpRuntimeException("Failed to initialize Qdrant connection: " + e.getMessage());
    }
  }

  private String validateAndExtractHost(IDataSinkParameterExtractor extractor)
      throws SpRuntimeException {
    String host = extractor.singleValueParameter(QDRANT_HOST_KEY, String.class);
    if (host == null || host.trim().isEmpty()) {
      throw new SpRuntimeException("Host cannot be empty");
    }
    return host;
  }

  private Integer validateAndExtractPort(IDataSinkParameterExtractor extractor)
      throws SpRuntimeException {
    Integer port = extractor.singleValueParameter(QDRANT_PORT_KEY, Integer.class);
    if (port == null || port < 1 || port > 65535) {
      throw new SpRuntimeException("Port must be between 1 and 65535");
    }
    return port;
  }

  private String validateAndExtractApiKey(IDataSinkParameterExtractor extractor)
      throws SpRuntimeException {
    String apiKey = extractor.singleValueParameter(QDRANT_API_KEY_KEY, String.class);
    if (apiKey == null || apiKey.trim().isEmpty()) {
      throw new SpRuntimeException("API key cannot be empty");
    }
    return apiKey;
  }

  private String validateAndExtractCollectionName(IDataSinkParameterExtractor extractor)
      throws SpRuntimeException {
    String collectionName = extractor.singleValueParameter(COLLECTION_NAME_KEY, String.class);
    if (collectionName == null || collectionName.trim().isEmpty()) {
      throw new SpRuntimeException("Collection name cannot be empty");
    }
    return collectionName;
  }

  private String validateAndExtractId(IDataSinkParameterExtractor extractor)
      throws SpRuntimeException {
    String id = extractor.singleValueParameter(ID_KEY, String.class);
    if (id == null || id.trim().isEmpty()) {
      throw new SpRuntimeException("ID field cannot be empty");
    }
    try {
      UUID.fromString(id);
    } catch (IllegalArgumentException e) {
      throw new SpRuntimeException("Invalid ID format. The ID must be a valid UUID string.");
    }
    return id;
  }

  private String validateAndExtractVectorField(IDataSinkParameterExtractor extractor)
      throws SpRuntimeException {
    String vectorField = extractor.mappingPropertyValue(VECTOR_NAME_KEY);
    if (vectorField == null || vectorField.trim().isEmpty()) {
      throw new SpRuntimeException("Vector field cannot be empty");
    }
    return vectorField.substring(4);
  }

  private Integer validateAndExtractDimension(IDataSinkParameterExtractor extractor)
      throws SpRuntimeException {
    Integer dimension =
        Integer.valueOf(extractor.singleValueParameter(VECTOR_DIMENSION_KEY, String.class));
    if (dimension == null || dimension <= 0) {
      throw new SpRuntimeException("Vector dimension must be a positive number");
    }
    return dimension;
  }

  private Distance validateAndExtractDistanceType(IDataSinkParameterExtractor extractor)
      throws SpRuntimeException {
    String distanceTypeStr = extractor.selectedSingleValue(VECTOR_DISTANCE_KEY, String.class);
    Distance distanceType = DISTANCE_TYPE_MAP.get(distanceTypeStr);
    if (distanceType == null) {
      throw new SpRuntimeException("Invalid distance type: " + distanceTypeStr);
    }
    return distanceType;
  }

  private void createOrValidateCollection() throws SpRuntimeException {
    try {
      var collectionExists = client.collectionExistsAsync(collectionName).get();
      if (!collectionExists) {

        client
            .createCollectionAsync(
                collectionName,
                Map.of(
                    vector,
                    VectorParams.newBuilder().setSize(dimension).setDistance(distanceType).build()))
            .get();
      }
    } catch (Exception e) {
      throw new SpRuntimeException("Failed to create or validate collection: " + e.getMessage());
    }
  }

  @Override
  public void onDetach() {
    if (client != null) {
      client.close();
    }
  }

  @Override
  public void onEvent(Event event) {
    if (event == null) {
      return;
    }

    final Map<String, Object> measurementValuePairs = event.getRaw();
    if (measurementValuePairs.size() <= 1) {
      return;
    }

    try {
      Map<String, Value> payload = new HashMap<>();
      List<Float> vectorValues = null;

      for (Map.Entry<String, Object> entry : measurementValuePairs.entrySet()) {
        final String name = entry.getKey();
        final Object value = entry.getValue();

        if (name.equals(vector)) {
          vectorValues = validateAndExtractVectorValues(value);
        } else if (value != null) {
          payload.put(name, QdrantValueFactory.value(value));
        }
      }

      if (vectorValues != null) {
        if (vectorValues.size() != dimension) {
          throw new SpRuntimeException(
              String.format(
                  "Vector dimension mismatch. Expected %d but got %d",
                  dimension, vectorValues.size()));
        }

        PointStruct point =
            PointStruct.newBuilder()
                .setId(PointIdFactory.id(UUID.fromString(id)))
                .setVectors(
                    Vectors.newBuilder()
                        .setVectors(
                            NamedVectors.newBuilder()
                                .putAllVectors(Map.of(vector, VectorFactory.vector(vectorValues)))
                                .build())
                        .build())
                .putAllPayload(payload)
                .build();

        client.upsertAsync(collectionName, Collections.singletonList(point)).get();
      } else {
        throw new SpRuntimeException("No vector values found in the event");
      }
    } catch (Exception e) {
      throw new SpRuntimeException("Error processing event: " + e.getMessage());
    }
  }

  private List<Float> validateAndExtractVectorValues(Object value) throws SpRuntimeException {
    if (!(value instanceof List)) {
      throw new SpRuntimeException("Vector field must be a list of numbers");
    }

    List<?> list = (List<?>) value;
    try {
      return list.stream()
          .map(
              item -> {
                if (item instanceof Number) {
                  return ((Number) item).floatValue();
                }
                throw new IllegalArgumentException("Vector must contain only numbers");
              })
          .collect(Collectors.toList());
    } catch (IllegalArgumentException e) {
      throw new SpRuntimeException("Invalid vector values: " + e.getMessage());
    }
  }
}
