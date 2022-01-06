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
import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.sinks.internal.jvm.config.ConfigKeys;
import org.apache.streampipes.svcdiscovery.api.SpConfig;
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

  private EventSchema eventSchema;

  @Override
  public void onInvocation(DataLakeParameters parameters, EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {
    LOG = parameters.getGraph().getLogger(DataLake.class);

    this.timestampField = parameters.getTimestampField();

    SpConfig configStore = runtimeContext.getConfigStore().getConfig();

    String influxHost = configStore.getString(ConfigKeys.DATA_LAKE_PROTOCOL) + "://" + configStore.getString(ConfigKeys.DATA_LAKE_HOST);
    Integer influxPort = configStore.getInteger(ConfigKeys.DATA_LAKE_PORT);
    String databaseName = configStore.getString(ConfigKeys.DATA_LAKE_DATABASE_NAME);
    String user = configStore.getString(ConfigKeys.DATA_LAKE_USERNAME);
    String password = configStore.getString(ConfigKeys.DATA_LAKE_PASSWORD);

    EventSchema schema = runtimeContext.getInputSchemaInfo().get(0).getEventSchema();
    // Remove the timestamp field from the event schema
    List<EventProperty> eventPropertiesWithoutTimestamp = schema.getEventProperties()
            .stream()
            .filter(eventProperty -> !this.timestampField.endsWith(eventProperty.getRuntimeName()))
            .collect(Collectors.toList());
    schema.setEventProperties(eventPropertiesWithoutTimestamp);

    // deep copy of event schema. Event property runtime name is changed to lower case for the schema registration
    this.eventSchema = new EventSchema(schema);

    schema.getEventProperties().forEach(eventProperty ->
            eventProperty.setRuntimeName(DataLakeUtils.sanitizePropertyRuntimeName(eventProperty.getRuntimeName())));
    registerAtDataLake(parameters.getMeasurementName(), schema, runtimeContext.getStreamPipesClient());

    imageProperties = schema.getEventProperties().stream()
            .filter(eventProperty -> eventProperty.getDomainProperties() != null &&
                    eventProperty.getDomainProperties().size() > 0 &&
                    eventProperty.getDomainProperties().get(0).toString().equals(SPSensor.IMAGE))
            .collect(Collectors.toList());

    imageDirectory = configStore.getString(ConfigKeys.IMAGE_STORAGE_LOCATION) + parameters.getMeasurementName() + "/";

    InfluxDbConnectionSettings settings = InfluxDbConnectionSettings.from(
            influxHost, influxPort, databaseName, parameters.getMeasurementName(), user, password);

    this.influxDbClient = new DataLakeInfluxDbClient(
            settings,
            parameters.getTimestampField(),
            parameters.getBatchSize(),
            parameters.getFlushDuration(),
            this.eventSchema,
            LOG
    );
  }

  @Override
  public void onEvent(Event event) {
    try {

      this.imageProperties.forEach(eventProperty -> {
        String eventTimestamp = Long.toString(event.getFieldBySelector(this.timestampField).getAsPrimitive().getAsLong());
        String fileRoute = this.imageDirectory + eventProperty.getRuntimeName() + "/" + eventTimestamp + ".png";
        String image = event.getFieldByRuntimeName(eventProperty.getRuntimeName()).getAsPrimitive().getAsString();

        this.writeToImageFile(image, fileRoute);
        fileRoute = fileRoute.replace("/", "_");
        fileRoute = fileRoute.replace("." , "_");
        event.updateFieldBySelector("s0::" + eventProperty.getRuntimeName(), fileRoute);
      });

      influxDbClient.save(event, this.eventSchema);
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
  private void registerAtDataLake(String measure,
                                  EventSchema eventSchema,
                                  StreamPipesClient client) throws SpRuntimeException {
      client
        .customRequest()
        .sendPost("api/v3/datalake/measure/" + measure, eventSchema);
  }


}
