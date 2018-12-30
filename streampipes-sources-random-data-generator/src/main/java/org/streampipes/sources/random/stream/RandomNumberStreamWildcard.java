/*
 * Copyright 2018 FZI Forschungszentrum Informatik
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
package org.streampipes.sources.random.stream;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.sdk.builder.DataStreamBuilder;
import org.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.streampipes.sdk.builder.WildcardTopicBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Formats;
import org.streampipes.sdk.helpers.Protocols;
import org.streampipes.sdk.utils.Datatypes;
import org.streampipes.sources.random.config.SourcesConfig;
import org.streampipes.sources.random.model.MessageConfig;
import org.streampipes.sources.random.model.MessageResult;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class RandomNumberStreamWildcard extends RandomNumberStream {

  private static final String SENSOR_ID_NAME = "sensorId";
  private static final String MACHINE_ID_NAME = "machineId";
  private static final String ASSEMBLY_LINE_ID_NAME = "assemblyLineId";
  private static final String FACILITY_ID_NAME = "facilityId";
  private static final String PRESSURE_NAME = "pressure";

  private static final List<String> sensorIds = Arrays.asList("sensor1", "sensor2");
  private static final List<String> machineIds = Arrays.asList("machine1", "machine2", "machine3");
  private static final List<String> assemblyLineIds = Arrays.asList("assemblyLine1", "assemblyLine2");
  private static final List<String> facilityIds = Arrays.asList("facility1", "facility2");

  @Override
  public SpDataStream declareModel(DataSourceDescription sep) {
    return DataStreamBuilder.create("random-number-wildcard", "Random Number Stream Wildcard Demo", "")
            .property(EpProperties.timestampProperty("timestamp"))
            .property(PrimitivePropertyBuilder
                    .create(Datatypes.String, SENSOR_ID_NAME)
                    .label("Sensor ID")
                    .description("The ID of the sensor")
                    .domainProperty("http://domain.prop/sensorId")
                    .scope(PropertyScope.DIMENSION_PROPERTY)
                    .valueSpecification("", "", sensorIds)
                    .build())
            .property(PrimitivePropertyBuilder
                    .create(Datatypes.String, MACHINE_ID_NAME)
                    .label("Machine ID")
                    .description("The ID of the machine")
                    .domainProperty("http://domain.prop/machineId")
                    .scope(PropertyScope.DIMENSION_PROPERTY)
                    .valueSpecification("", "", machineIds)
                    .build())
            .property(PrimitivePropertyBuilder
                    .create(Datatypes.String, ASSEMBLY_LINE_ID_NAME)
                    .label("Assembly Line ID")
                    .description("The ID of the assembly line")
                    .domainProperty("http://domain.prop/assemblyLineId")
                    .scope(PropertyScope.DIMENSION_PROPERTY)
                    .valueSpecification("", "", assemblyLineIds)
                    .build())
            .property(PrimitivePropertyBuilder
                    .create(Datatypes.String, FACILITY_ID_NAME)
                    .label("Facility ID")
                    .description("The ID of the facility")
                    .domainProperty("http://domain.prop/facilityId")
                    .valueSpecification("", "", facilityIds)
                    .scope(PropertyScope.DIMENSION_PROPERTY)
                    .build())
            .property(PrimitivePropertyBuilder
                    .create(Datatypes.Float, PRESSURE_NAME)
                    .label("Pressure")
                    .description("Measures the current pressure")
                    .domainProperty("http://domain.prop/pressure")
                    .measurementUnit(URI.create("http://qudt.org/vocab/unit#Kilogram"))
                    .scope(PropertyScope.MEASUREMENT_PROPERTY)
                    .build())
            .format(Formats.jsonFormat())
            .protocol(Protocols.kafka(SourcesConfig.INSTANCE.getKafkaHost(), SourcesConfig.INSTANCE.getKafkaPort(),
                    WildcardTopicBuilder
                            .create("org.streampipes.company.$facilityId.$assemblyLineId.$afagor"
                                    + ".$machineId.$pressure.$sensorId")
                            .addLocationIdMapping(FACILITY_ID_NAME)
                            .addLocationIdMapping(ASSEMBLY_LINE_ID_NAME)
                            .addSensorIdMapping(SENSOR_ID_NAME)
                            .addPlatformIdMapping(MACHINE_ID_NAME)
                            .addPlatformTypeMapping("afagor")
                            .addSensorTypeMapping("pressure")
                            .build()))
            .build();
  }

  @Override
  protected MessageResult getMessage(MessageConfig messageConfig) {
    try {
      JSONObject message = buildJson(messageConfig.getTimestamp());
      return new MessageResult(message
              .toString()
              .getBytes(), getTopic(message));
    } catch (JSONException e) {
      e.printStackTrace();
      return new MessageResult(false);
    }
  }

  private String getTopic(JSONObject jsonObject) throws JSONException {
    return "org.streampipes.company."
            + jsonObject.getString(FACILITY_ID_NAME)
            + "."
            + jsonObject.getString(ASSEMBLY_LINE_ID_NAME)
            + ".afagor"
            + "."
            + jsonObject.getString(MACHINE_ID_NAME)
            + ".pressure."
            + jsonObject.getString(SENSOR_ID_NAME);
  }

  private JSONObject buildJson(long timestamp) throws JSONException {
    JSONObject json = new JSONObject();
    json.put("timestamp", timestamp);
    json.put(SENSOR_ID_NAME, getRandom(sensorIds));
    json.put(MACHINE_ID_NAME, getRandom(machineIds));
    json.put(FACILITY_ID_NAME, getRandom(facilityIds));
    json.put(ASSEMBLY_LINE_ID_NAME, getRandom(assemblyLineIds));
    json.put(PRESSURE_NAME, random.nextInt(10));
    return json;
  }

  private String getRandom(List<String> values) {
    return values.get(random.nextInt(values.size()));
  }


  public static void main(String[] args) {
    new RandomNumberStreamJson().executeStream();
  }
}
