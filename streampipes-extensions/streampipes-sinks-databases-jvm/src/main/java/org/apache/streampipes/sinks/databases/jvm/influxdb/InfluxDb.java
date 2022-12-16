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

package org.apache.streampipes.sinks.databases.jvm.influxdb;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.context.EventSinkRuntimeContext;
import org.apache.streampipes.wrapper.runtime.EventSink;

public class InfluxDb implements EventSink<InfluxDbParameters> {

  private InfluxDbClient influxDbClient;

  private static Logger log;

  @Override
  public void onInvocation(InfluxDbParameters parameters, EventSinkRuntimeContext runtimeContext)
      throws SpRuntimeException {
    log = parameters.getGraph().getLogger(InfluxDb.class);

    this.influxDbClient = new InfluxDbClient(
        parameters.getInfluxDbHost(),
        parameters.getInfluxDbPort(),
        parameters.getDatabaseName(),
        parameters.getMeasurementName(),
        parameters.getUsername(),
        parameters.getPassword(),
        parameters.getTimestampField(),
        parameters.getBatchSize(),
        parameters.getFlushDuration(),
        log
    );
  }

  @Override
  public void onEvent(Event event) {
    try {
      influxDbClient.save(event);
    } catch (SpRuntimeException e) {
      log.error(e.getMessage());
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {
    influxDbClient.stop();
  }

  public static String prepareString(String s) {
    return s.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
  }
}
