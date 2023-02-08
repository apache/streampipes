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

import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.field.AbstractField;
import org.apache.streampipes.wrapper.context.EventSinkRuntimeContext;
import org.apache.streampipes.wrapper.runtime.EventSink;

import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.session.pool.SessionPool;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.iotdb.tsfile.utils.Binary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IotDb implements EventSink<IotDbParameters> {

  private static Logger logger;

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
  public void onInvocation(IotDbParameters parameters, EventSinkRuntimeContext runtimeContext) {
    logger = parameters.getGraph().getLogger(IotDb.class);

    deviceId = "root." + parameters.getDatabase() + "." + parameters.getDevice();
    timestampFieldId = parameters.getTimestampField();

    // In our case, the pool size is set to 2.
    // One connection is for current requests, and the other is a backup for fast-recovery when connection dies.
    sessionPool = new SessionPool.Builder().maxSize(2).enableCompression(false).host(parameters.getHost())
        .port(parameters.getPort()).user(parameters.getUser()).password(parameters.getPassword()).build();
  }

  @Override
  public void onEvent(Event event) {
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
      logger.error("Failed to save event to IoTDB, because: " + e.getMessage());
      e.printStackTrace();
    }
  }

  @Override
  public void onDetach() {
    sessionPool.close();
  }
}
