/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.sinks.internal.jvm.datalake;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.logging.api.Logger;
import org.streampipes.model.runtime.Event;
import org.streampipes.wrapper.context.EventSinkRuntimeContext;
import org.streampipes.wrapper.runtime.EventSink;

/**
 * Code is the same as InfluxDB (org.streampipes.sinks.databases.jvm.influxdb) sink. Changes applied here should also be applied in the InfluxDB sink
 */
public class DataLake implements EventSink<DataLakeParameters> {


  private InfluxDbClient influxDbClient;

  private static Logger LOG;

  @Override
  public void onInvocation(DataLakeParameters parameters, EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {
    LOG = parameters.getGraph().getLogger(DataLake.class);

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
        LOG
    );
  }

  @Override
  public void onEvent(Event event) {
    try {
      influxDbClient.save(event);
    } catch (SpRuntimeException e) {
      LOG.error(e.getMessage());
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {
    influxDbClient.stop();
  }
}
