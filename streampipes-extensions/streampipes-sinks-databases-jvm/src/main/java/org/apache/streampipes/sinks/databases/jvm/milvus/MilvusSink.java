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

package org.apache.streampipes.sinks.databases.jvm.milvus;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataSink;
import org.apache.streampipes.extensions.api.pe.config.IDataSinkConfiguration;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataSinkParameters;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyNested;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.builder.sink.DataSinkConfiguration;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.vocabulary.XSD;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.milvus.param.Constant;
import io.milvus.pool.MilvusClientV2Pool;
import io.milvus.pool.PoolConfig;
import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.common.DataType;
import io.milvus.v2.common.IndexParam;
import io.milvus.v2.service.collection.request.AddFieldReq;
import io.milvus.v2.service.collection.request.CreateCollectionReq;
import io.milvus.v2.service.collection.request.DescribeCollectionReq;
import io.milvus.v2.service.collection.request.HasCollectionReq;
import io.milvus.v2.service.collection.response.DescribeCollectionResp;
import io.milvus.v2.service.database.request.CreateDatabaseReq;
import io.milvus.v2.service.database.response.ListDatabasesResp;
import io.milvus.v2.service.vector.request.InsertReq;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MilvusSink implements IStreamPipesDataSink {
  public static final String MILVUS_URI_KEY = "milvus_uri";
  public static final String MILVUS_TOKEN_KEY = "milvus_token";

  public static final String MILVUS_DBNAME_KEY = "milvus_dbname";
  public static final String DATABASE_REPLICA_NUMBER_KEY = "database_replica_number";

  public static final String COLLECTION_NAME_KEY = "collection_name";

  public static final String VECTOR_KEY = "vector";
  public static final String DIMENSION = "dimension";
  public static final String METRIC_TYPE = "metric_type";

  public static final String INDEX = "index";

  public static final String PRIMARY = "primary";

  public DataType vectorDataType;

  public final Gson gson = new Gson();

  public MilvusClientV2Pool pool;
  public MilvusClientV2 client;
  String vector;
  String primary;
  String collectionName;
  Integer dimension;
  IndexParam.MetricType metricType;
  IndexParam indexParam;
  CreateCollectionReq.CollectionSchema collectionSchema;

  public static final String BYTE = XSD.BYTE.toString();
  public static final String SHORT = XSD.SHORT.toString();
  public static final String LONG = XSD.LONG.toString();
  public static final String INT =  XSD.INT.toString();
  public static final String FLOAT = XSD.FLOAT.toString();
  public static final String DOUBLE = XSD.DOUBLE.toString();
  public static final String BOOLEAN = XSD.BOOLEAN.toString();
  public static final String STRING = XSD.STRING.toString();


  private static final Map<String, DataType> INDEX_MAP = new HashMap<>() {
    {
      put("BinaryVector", DataType.BinaryVector);
      put("FloatVector", DataType.FloatVector);
    }
  };

  private static final Map<String, IndexParam.MetricType> METRIC_TYPE_MAP = new HashMap<>() {
    {
      put("L2", IndexParam.MetricType.L2);
      put("IP", IndexParam.MetricType.IP);
      put("COSINE", IndexParam.MetricType.COSINE);
      put("HAMMING", IndexParam.MetricType.HAMMING);
      put("JACCARD", IndexParam.MetricType.JACCARD);
      put("BM25", IndexParam.MetricType.BM25);
    }
  };

  @Override
  public IDataSinkConfiguration declareConfig() {
    return DataSinkConfiguration.create(
        MilvusSink::new,
        DataSinkBuilder
            .create("org.apache.streampipes.sinks.databases.jvm.milvus", 0)
            .withLocales(Locales.EN)
            .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON).
            category(DataSinkType.DATABASE)
            .requiredTextParameter(Labels.withId(MILVUS_URI_KEY))
            .requiredTextParameter(Labels.withId(MILVUS_TOKEN_KEY), "root:Milvus")
            .requiredTextParameter(Labels.withId(MILVUS_DBNAME_KEY))
            .requiredTextParameter(Labels.withId(DATABASE_REPLICA_NUMBER_KEY), "2")
            .requiredTextParameter(Labels.withId(COLLECTION_NAME_KEY))
            .requiredTextParameter(Labels.withId(PRIMARY), "id")
            .requiredIntegerParameter(Labels.withId(DIMENSION), 2)
            .requiredStream(StreamRequirementsBuilder
                .create()
                .requiredPropertyWithUnaryMapping(EpRequirements.anyProperty(),
                Labels.withId(VECTOR_KEY),
                PropertyScope.NONE)
            .build())
        .requiredSingleValueSelection(Labels.withId(INDEX),
             Options.from(INDEX_MAP.keySet().toArray(new String[0])))
        .requiredSingleValueSelection(Labels.withId(METRIC_TYPE),
             Options.from(METRIC_TYPE_MAP.keySet().toArray(new String[0])))
        .build()
    );
  }

  @Override
  public void onPipelineStarted(IDataSinkParameters parameters,
                                EventSinkRuntimeContext runtimeContext) {
    var extractor = parameters.extractor();
    final String uri = extractor.singleValueParameter(MILVUS_URI_KEY, String.class);
    final String token = extractor.singleValueParameter(MILVUS_TOKEN_KEY, String.class);

    ConnectConfig connectConfig = ConnectConfig.builder()
        .uri(uri)
        .token(token)
        .build();

    PoolConfig poolConfig = PoolConfig.builder()
        .maxIdlePerKey(10) // max idle clients per key
        .maxTotalPerKey(20) // max total(idle + active) clients per key
        .maxTotal(100) // max total clients for all keys
        .maxBlockWaitDuration(Duration.ofSeconds(5L)) // getClient() will wait 5 seconds if no idle client available
        .minEvictableIdleDuration(Duration.ofSeconds(10L)) // if number
        .build();

    try {
      pool = new MilvusClientV2Pool(poolConfig, connectConfig);
      client = pool.getClient("client_name");
      //create a dataBase
      final String dbName = parameters.extractor().singleValueParameter(MILVUS_DBNAME_KEY, String.class);
      final String dbReplicaNum =
              parameters.extractor().singleValueParameter(DATABASE_REPLICA_NUMBER_KEY, String.class);
      Map<String, String> properties = new HashMap<>();
      properties.put(Constant.DATABASE_REPLICA_NUMBER, dbReplicaNum);
      ListDatabasesResp listDatabasesResp = client.listDatabases();
      List<String> dbNames = listDatabasesResp.getDatabaseNames();
      if (!dbNames.contains(dbName)) {
        CreateDatabaseReq createDatabaseReq = CreateDatabaseReq.builder()
            .databaseName(dbName)
            .properties(properties)
            .build();
        client.createDatabase(createDatabaseReq);
        client.useDatabase(dbName);
      } else {
        client.useDatabase(dbName);
      }

      this.vector = parameters.extractor().mappingPropertyValue(VECTOR_KEY).substring(4);
      this.vectorDataType = INDEX_MAP.get(parameters.extractor().selectedSingleValue(INDEX, String.class));
      this.primary = parameters.extractor().singleValueParameter(PRIMARY, String.class);
      this.dimension = Integer.valueOf(parameters.extractor().singleValueParameter(DIMENSION, String.class));
      this.metricType = METRIC_TYPE_MAP.get(parameters.extractor().selectedSingleValue(METRIC_TYPE, String.class));
      this.collectionName = parameters.extractor().singleValueParameter(COLLECTION_NAME_KEY, String.class);

      // check whether collection test exists
      HasCollectionReq hasCollectionReq = HasCollectionReq.builder()
          .collectionName(this.collectionName)
          .build();
      Boolean resp = client.hasCollection(hasCollectionReq);
      if (resp) {
        DescribeCollectionReq describeCollectionReq = DescribeCollectionReq.builder()
            .collectionName(this.collectionName)
            .build();
        DescribeCollectionResp describeCollectionResp = client.describeCollection(describeCollectionReq);
        if (!validateEventSchema(parameters.getModel().getInputStreams().get(0).getEventSchema().getEventProperties(),
                "", describeCollectionResp.getCollectionSchema())){
          throw new SpRuntimeException("The schema of the collection does not match the schema of the event stream");
        }
      } else {
        // create a collection with schema, when indexParams is specified, it will create index as well
        collectionSchema = client.createSchema();
        EventSchema schema = parameters.getModel().getInputStreams().get(0).getEventSchema();
        this.extractEventProperties(schema.getEventProperties(), "", collectionSchema);
        indexParam = IndexParam.builder()
            .fieldName(vector)
            .metricType(metricType)
            .build();
        CreateCollectionReq createCollectionReq = CreateCollectionReq.builder()
            .collectionName(collectionName)
            .collectionSchema(collectionSchema)
            .indexParams(Collections.singletonList(indexParam))
            .build();
        client.createCollection(createCollectionReq);
      }
    } catch (Exception e) {
      throw new SpRuntimeException(e.getMessage());
    }
  }

  @Override
  public void onPipelineStopped() {
    client.close();
    pool.close();
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

    JsonObject data = new JsonObject();
    for (Map.Entry<String, Object> measurementValuePair : measurementValuePairs.entrySet()) {
      final String name = measurementValuePair.getKey();
      final Object value = measurementValuePair.getValue();
      if (name.equals(primary)) {
        data.addProperty(name, value.toString());
      } else if (name.equals("FloatVector")) {
        if (value instanceof List) {
          List<Float> floatValues = (List<Float>) value;
          data.add(name, gson.toJsonTree(floatValues));
        }
      } else if (name.equals("BinaryVector")) {
        if (value instanceof List) {
          List<Byte> binaryValues = (List<Byte>) value;
          data.add(name, gson.toJsonTree(binaryValues));
        }
      } else {
        data.add(name, gson.toJsonTree(value));
      }
    }
    InsertReq insertReq = InsertReq.builder()
        .collectionName(collectionName)
        .data(Collections.singletonList(data))
        .build();
    client.insert(insertReq);
  }

  private void extractEventProperties(List<EventProperty> properties, String preProperty,
                                    CreateCollectionReq.CollectionSchema collectionSchema)
        throws SpRuntimeException {
    for (EventProperty property : properties) {
      final String name = preProperty + property.getRuntimeName();
      if (property instanceof EventPropertyNested) {
        extractEventProperties(((EventPropertyNested) property).getEventProperties(),
                name + "_", collectionSchema);
      } else {
        if (property instanceof EventPropertyPrimitive) {
          initField(name, ((EventPropertyPrimitive) property).getRuntimeType(), collectionSchema);
        }
      }
    }
  }

  private void initField(final String name , final String uri,
                         final CreateCollectionReq.CollectionSchema collectionSchema){
    if (name.equals(this.vector)) {
      initField(collectionSchema, name, this.vectorDataType);
    } else {
      if (uri.equals(BYTE)){
        initField(collectionSchema, name, DataType.Int8);
      } else if (uri.equals(SHORT)){
        initField(collectionSchema, name, DataType.Int16);
      } else if (uri.equals(INT)){
        initField(collectionSchema, name, DataType.Int32);
      } else if (uri.equals(LONG)){
        initField(collectionSchema, name, DataType.Int64);
      } else if (uri.equals(DOUBLE)){
        initField(collectionSchema, name, DataType.Double);
      } else if (uri.equals(FLOAT)){
        initField(collectionSchema, name, DataType.Float);
      } else if (uri.equals(BOOLEAN)){
        initField(collectionSchema, name, DataType.Bool);
      } else if (uri.equals(STRING)){
        initField(collectionSchema, name, DataType.VarChar);
      }
    }
  }

  private void initField(final CreateCollectionReq.CollectionSchema collectionSchema,
                         final String name, DataType dataType) {
    if (name.equals(this.primary)) {
      collectionSchema.addField(AddFieldReq.builder().fieldName(name).dataType(dataType)
              .isPrimaryKey(true).autoID(Boolean.FALSE).description(primary).build());
    } else if (name.equals(this.vector)) {
      collectionSchema.addField(AddFieldReq.builder().fieldName(name).dataType(dataType).dimension(dimension).build());
    } else {
      collectionSchema.addField(AddFieldReq.builder().fieldName(name).dataType(dataType).build());
    }
  }

  private boolean validateEventSchema(List<EventProperty> properties, String preProperty,
                                    CreateCollectionReq.CollectionSchema collectionSchema)
          throws SpRuntimeException {
    for (EventProperty property : properties) {
      final String name = preProperty + property.getRuntimeName();
      if (property instanceof EventPropertyNested) {
        if (!validateEventSchema(((EventPropertyNested) property).getEventProperties(),
                name + "_", collectionSchema)) {
          return false;
        }
      } else {
        if (property instanceof EventPropertyPrimitive) {
          final boolean result = validateField(name,
                  ((EventPropertyPrimitive) property).getRuntimeType(), collectionSchema);
          if (!result) {
            return false;
          }
        }
      }
    }
    return true;
  }

  private boolean validateField(final String name, final String uri,
                                final CreateCollectionReq.CollectionSchema collectionSchema) {
    if (name.equals(this.vector)){
      return validateField(collectionSchema, name, this.vectorDataType);
    } else {
      if (uri.equals(BYTE)){
        return validateField(collectionSchema, name, DataType.Int8);
      } else if (uri.equals(SHORT)){
        return validateField(collectionSchema, name, DataType.Int16);
      } else if (uri.equals(INT)){
        return validateField(collectionSchema, name, DataType.Int32);
      } else if (uri.equals(LONG)){
        return validateField(collectionSchema, name, DataType.Int64);
      } else if (uri.equals(DOUBLE)){
        return validateField(collectionSchema, name, DataType.Double);
      } else if (uri.equals(FLOAT)){
        return validateField(collectionSchema, name, DataType.Float);
      } else if (uri.equals(BOOLEAN)){
        return validateField(collectionSchema, name, DataType.Bool);
      } else if (uri.equals(STRING)){
        return validateField(collectionSchema, name, DataType.VarChar);
      }
      return false;
    }
  }

  private boolean validateField(final CreateCollectionReq.CollectionSchema collectionSchema,
                                final String name, DataType dataType) {
    final CreateCollectionReq.FieldSchema fieldSchema = collectionSchema.getField(name);
    final boolean result = fieldSchema != null && fieldSchema.getDataType().equals(dataType);
    if (name.equals(this.primary)) {
      return result && fieldSchema.getIsPrimaryKey();
    } else if (name.equals(this.vector)){
      return result && fieldSchema.getDataType().equals(this.vectorDataType);
    }
    return result;
  }
}