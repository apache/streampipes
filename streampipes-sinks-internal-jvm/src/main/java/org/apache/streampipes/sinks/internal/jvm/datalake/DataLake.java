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

package org.apache.streampipes.sinks.internal.jvm.datalake;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.serializers.json.Utils;
import org.apache.streampipes.sinks.internal.jvm.config.SinksInternalJvmConfig;
import org.apache.streampipes.wrapper.context.EventSinkRuntimeContext;
import org.apache.streampipes.wrapper.runtime.EventSink;

import java.io.IOException;

/**
 * Code is the same as InfluxDB (org.apache.streampipes.sinks.databases.jvm.influxdb) sink. Changes applied here should also be applied in the InfluxDB sink
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
        parameters.getDimensionProperties(),
        LOG
    );
    registerAtDataLake(parameters.getMeasurementName(), runtimeContext.getInputSchemaInfo().get(0).getEventSchema());


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

  private void registerAtDataLake(String measure, EventSchema eventSchema) throws SpRuntimeException {
    HttpClient httpClient = new DefaultHttpClient();
    String url = SinksInternalJvmConfig.INSTANCE.getStreamPipesBackendUrl();
    HttpPost httpPost = new HttpPost(url + "/streampipes-backend/api/v3/noauth/datalake/" + measure);
    httpPost.setHeader("Content-type", "application/json");

    String json = Utils.getGson().toJson(eventSchema);
    StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
    httpPost.setEntity(stringEntity);

    try {
      HttpResponse response = httpClient.execute(httpPost);
      if (response.getStatusLine().getStatusCode() == 409) {
        throw new SpRuntimeException("The measuremnt '" + measure +"' is already registered as Data lake with different Event schema");
      }
    } catch (IOException e) {
      LOG.error(e.toString());
    }

  }
}
