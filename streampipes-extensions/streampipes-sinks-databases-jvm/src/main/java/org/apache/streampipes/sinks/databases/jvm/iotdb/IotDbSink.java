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

package org.apache.streampipes.sinks.databases.jvm.iotdb;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.field.AbstractField;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.SinkParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataSink;

import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.session.pool.SessionPool;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.iotdb.tsfile.utils.Binary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IotDbSink extends StreamPipesDataSink {

  private static final Logger LOG = LoggerFactory.getLogger(IotDbSink.class);

  private static final String HOST_KEY = "db_host";
  private static final String PORT_KEY = "db_port";

  private static final String DATABASE_KEY = "db_database";
  private static final String DEVICE_KEY = "db_device";

  private static final String USER_KEY = "db_user";
  private static final String PASSWORD_KEY = "db_password";

  private static final String TIMESTAMP_MAPPING_KEY = "timestamp_mapping";

  private String timestampFieldId;
  private String deviceId;

  // IoTDB provides a connection pool (SessionPool) for Native API.
  // Using the interface, we need to define the pool size.
  // If we can not get a session connection in 60 seconds, there is a warning log but the program will hang.
  // If a session has finished an operation, it will be put back to the pool automatically.
  // If a session connection is broken, the session will be removed automatically and the pool will try to create a
  // new session and redo the operation.
  private SessionPool sessionPool;

  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder.create("org.apache.streampipes.sinks.databases.jvm.iotdb").withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON).category(DataSinkType.DATABASE).requiredStream(
            StreamRequirementsBuilder.create()
                .requiredPropertyWithUnaryMapping(EpRequirements.timestampReq(), Labels.withId(TIMESTAMP_MAPPING_KEY),
                    PropertyScope.NONE).build()).requiredTextParameter(Labels.withId(HOST_KEY))
        .requiredIntegerParameter(Labels.withId(PORT_KEY), 6667).requiredTextParameter(Labels.withId(USER_KEY), "root")
        .requiredSecret(Labels.withId(PASSWORD_KEY)).requiredTextParameter(Labels.withId(DATABASE_KEY))
        .requiredTextParameter(Labels.withId(DEVICE_KEY)).build();
  }

  @Override
  public void onInvocation(SinkParams parameters,
                           EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {
    var extractor = parameters.extractor();
    final String host = extractor.singleValueParameter(HOST_KEY, String.class);
    final Integer port = extractor.singleValueParameter(PORT_KEY, Integer.class);

    final String database = extractor.singleValueParameter(DATABASE_KEY, String.class);
    final String device = extractor.singleValueParameter(DEVICE_KEY, String.class);

    final String user = extractor.singleValueParameter(USER_KEY, String.class);
    final String password = extractor.secretValue(PASSWORD_KEY);

    timestampFieldId = extractor.mappingPropertyValue(TIMESTAMP_MAPPING_KEY);
    deviceId = "root." + database + "." + device;

    // In our case, the pool size is set to 2.
    // One connection is for current requests, and the other is a backup for fast-recovery when connection dies.
    sessionPool = new SessionPool.Builder()
        .maxSize(2)
        .enableCompression(false)
        .host(host)
        .port(port)
        .user(user)
        .password(password)
        .build();
  }

  @Override
  public void onEvent(Event event) throws SpRuntimeException {
    if (event == null) {
      return;
    }

    final AbstractField timestampAbstractField = event.getFieldBySelector(timestampFieldId);
    final Long timestamp = timestampAbstractField.getAsPrimitive().getAsLong();
    if (timestamp == null) {
      return;
    }

    final Map<String, Object> measurementValuePairs = event.getRaw();
    // should be at least a timestamp field and a measurement field
    if (measurementValuePairs.size() <= 1) {
      return;
    }

    final int measurementFieldCount = measurementValuePairs.size() - 1;
    final List<String> measurements = new ArrayList<>(measurementFieldCount);
    final List<TSDataType> types = new ArrayList<>(measurementFieldCount);
    final List<Object> values = new ArrayList<>(measurementFieldCount);

    for (Map.Entry<String, Object> measurementValuePair : measurementValuePairs.entrySet()) {
      if (timestampAbstractField.getFieldNameIn().equals(measurementValuePair.getKey())) {
        continue;
      }

      measurements.add(measurementValuePair.getKey());

      final Object value = measurementValuePair.getValue();
      if (value instanceof Integer) {
        types.add(TSDataType.INT32);
        values.add(value);
      } else if (value instanceof Long) {
        types.add(TSDataType.INT64);
        values.add(value);
      } else if (value instanceof Float) {
        types.add(TSDataType.FLOAT);
        values.add(value);
      } else if (value instanceof Double) {
        types.add(TSDataType.DOUBLE);
        values.add(value);
      } else if (value instanceof Boolean) {
        types.add(TSDataType.BOOLEAN);
        values.add(value);
      } else {
        types.add(TSDataType.TEXT);
        values.add(Binary.valueOf(value.toString()));
      }
    }

    try {
      sessionPool.insertRecord(deviceId, timestamp, measurements, types, values);
    } catch (IoTDBConnectionException | StatementExecutionException e) {
      LOG.error("Failed to save event to IoTDB, because: " + e.getMessage());
      e.printStackTrace();
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {
    sessionPool.close();
  }
}
