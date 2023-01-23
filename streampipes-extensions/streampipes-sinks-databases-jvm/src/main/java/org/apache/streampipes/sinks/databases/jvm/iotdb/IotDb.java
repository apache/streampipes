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
import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.context.EventSinkRuntimeContext;
import org.apache.streampipes.wrapper.runtime.EventSink;

import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.session.Session;
import org.apache.iotdb.session.util.Version;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.iotdb.tsfile.utils.Binary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IotDb implements EventSink<IotDbParameters> {

  private static Logger logger;

  private String timestampField;
  private String deviceId;
  private Session session;

  @Override
  public void onInvocation(IotDbParameters parameters, EventSinkRuntimeContext runtimeContext)
      throws SpRuntimeException {
    logger = parameters.getGraph().getLogger(IotDb.class);
    deviceId = "root." + parameters.getDatabase() + "." + parameters.getDevice();
    timestampField = parameters.getTimestampField();

    session = new Session.Builder().host(parameters.getHost()).port(parameters.getPort()).username(parameters.getUser())
        .password(parameters.getPassword()).version(Version.V_0_13).build();
    try {
      session.open(false);
    } catch (IoTDBConnectionException e) {
      throw new SpRuntimeException("Failed to create connection to IoTDB server. ", e);
    }
  }

  @Override
  public void onEvent(Event event) {
    if (event == null) {
      return;
    }

    final Long timestamp = event.getFieldBySelector(timestampField).getAsPrimitive().getAsLong();

    final Map<String, Object> measurementValuePairs = event.getRaw();
    final List<String> measurements = new ArrayList<>(measurementValuePairs.size());
    final List<TSDataType> types = new ArrayList<>(measurementValuePairs.size());
    final Object[] values = new Object[measurementValuePairs.size()];

    int valueIndex = 0;
    for (Map.Entry<String, Object> measurementValuePair : measurementValuePairs.entrySet()) {
      measurements.add(measurementValuePair.getKey());

      final Object value = measurementValuePair.getValue();
      if (value instanceof Integer) {
        types.add(TSDataType.INT32);
        values[valueIndex++] = value;
      } else if (value instanceof Long) {
        types.add(TSDataType.INT64);
        values[valueIndex++] = value;
      } else if (value instanceof Float) {
        types.add(TSDataType.FLOAT);
        values[valueIndex++] = value;
      } else if (value instanceof Double) {
        types.add(TSDataType.DOUBLE);
        values[valueIndex++] = value;
      } else if (value instanceof Boolean) {
        types.add(TSDataType.BOOLEAN);
        values[valueIndex++] = value;
      } else {
        types.add(TSDataType.TEXT);
        values[valueIndex++] = Binary.valueOf(value.toString());
      }
    }

    try {
      session.insertRecord(deviceId, timestamp, measurements, types, values);
    } catch (IoTDBConnectionException | StatementExecutionException e) {
      logger.error("Failed to save event to IoTDB, because: " + e.getMessage());
      e.printStackTrace();
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {
    try {
      session.close();
    } catch (IoTDBConnectionException e) {
      throw new SpRuntimeException("Failed to close IoTDB session. ", e);
    }
  }
}
