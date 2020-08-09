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

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.sinks.internal.jvm.config.SinksInternalJvmConfig;
import org.apache.streampipes.vocabulary.SPSensor;
import org.apache.streampipes.wrapper.context.EventSinkRuntimeContext;
import org.apache.streampipes.wrapper.runtime.EventSink;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Code is the same as InfluxDB (org.apache.streampipes.sinks.databases.jvm.influxdb) sink. Changes applied here should also be applied in the InfluxDB sink
 */
public class DataLake implements EventSink<DataLakeParameters> {


  private DataLakeInfluxDbClient influxDbClient;

  private static Logger LOG;

  private List<EventProperty> imageProperties;

  private String imageDirectory;

  private String timestampField;

  @Override
  public void onInvocation(DataLakeParameters parameters, EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {
    LOG = parameters.getGraph().getLogger(DataLake.class);

    this.timestampField = parameters.getTimestampField();

    this.influxDbClient = new DataLakeInfluxDbClient(
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

    EventSchema schema = runtimeContext.getInputSchemaInfo().get(0).getEventSchema();

    schema.getEventProperties().stream().forEach(eventProperty -> {
      eventProperty.setRuntimeName(prepareString(eventProperty.getRuntimeName()));
    });
    registerAtDataLake(parameters.getMeasurementName(), schema);

    imageProperties = schema.getEventProperties().stream()
            .filter(eventProperty -> eventProperty.getDomainProperties() != null &&
                    eventProperty.getDomainProperties().size() > 0 &&
                    eventProperty.getDomainProperties().get(0).toString().equals(SPSensor.IMAGE))
            .collect(Collectors.toList());

    imageDirectory = SinksInternalJvmConfig.INSTANCE.getImageStorageLocation() + parameters.getMeasurementName() + "/";

  }

  @Override
  public void onEvent(Event event) {
    try {

      this.imageProperties.stream().forEach(eventProperty -> {
        String eventTimestamp = Long.toString(event.getFieldBySelector(this.timestampField).getAsPrimitive().getAsLong());
        String fileRoute = this.imageDirectory + eventProperty.getRuntimeName() + "/" + eventTimestamp + ".png";
        String image = event.getFieldByRuntimeName(eventProperty.getRuntimeName()).getAsPrimitive().getAsString();

        this.writeToImageFile(image, fileRoute);
        fileRoute = fileRoute.replace("/", "_");
        fileRoute = fileRoute.replace("." , "_");
        event.updateFieldBySelector("s0::" + eventProperty.getRuntimeName(), fileRoute);
      });

      event.addField("sp_internal_label", "");

      influxDbClient.save(event);
    } catch (SpRuntimeException e) {
      LOG.error(e.getMessage());
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {
    influxDbClient.stop();
  }

  private void writeToImageFile(String image, String fileRoute) {
    byte[] data = Base64.decodeBase64(image);
    try {
      File file = new File(fileRoute);
      file.getParentFile().mkdirs();
      OutputStream stream = new FileOutputStream(file, false);
      stream.write(data);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  /**
   * Adds a new measurement to the StreamPipes data lake
   * @param measure
   * @param eventSchema
   * @throws SpRuntimeException
   */
  private void registerAtDataLake(String measure, EventSchema eventSchema) throws SpRuntimeException {
    String url = SinksInternalJvmConfig.INSTANCE.getStreamPipesBackendUrl();

    try {
      String json = JacksonSerializer.getObjectMapper().writeValueAsString(eventSchema);
      StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
      HttpResponse response = Request.Post(url + "/streampipes-backend/api/v3/noauth/datalake/" + measure)
              .addHeader("Content-type", "application/json")
              .body(stringEntity)
              .execute()
              .returnResponse();
      if (response.getStatusLine().getStatusCode() == 409) {
        throw new SpRuntimeException("The measurement '" + measure +"' is already registered as Data lake with different Event schema");
      }
    } catch (IOException e) {
      LOG.error(e.toString());
    }

  }

  public static String prepareString(String s) {
    return s.toLowerCase().replaceAll(" ", "_");
  }
}
