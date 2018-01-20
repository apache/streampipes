/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.pe.sources.samples.random;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.model.grounding.TopicParameterType;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.pe.sources.samples.config.SourcesConfig;
import org.streampipes.sdk.builder.DataStreamBuilder;
import org.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.streampipes.sdk.builder.WildcardTopicBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Formats;
import org.streampipes.sdk.helpers.Protocols;
import org.streampipes.sdk.utils.Datatypes;

import java.util.Optional;

public class RandomNumberStreamWildcard extends RandomNumberStream {

  public static final String TOPIC = "SEPA.SEP.Random.Number.Json";

  public RandomNumberStreamWildcard() {
    super(TOPIC);
  }

  private static final String SENSOR_ID_NAME = "sensorId";
  private static final String MACHINE_ID_NAME = "machineId";
  private static final String ASSEMBLY_LINE_ID_NAME = "assemblyLineId";
  private static final String FACILITY_ID_NAME = "facilityId";

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
                    .build())
            .property(PrimitivePropertyBuilder
                    .create(Datatypes.String, MACHINE_ID_NAME)
                    .label("Machine ID")
                    .description("The ID of the machine")
                    .domainProperty("http://domain.prop/machineId")
                    .scope(PropertyScope.DIMENSION_PROPERTY)
                    .build())
            .property(PrimitivePropertyBuilder
                    .create(Datatypes.String, ASSEMBLY_LINE_ID_NAME)
                    .label("Sensor ID")
                    .description("The ID of the assembly line")
                    .domainProperty("http://domain.prop/assemblyLineId")
                    .scope(PropertyScope.DIMENSION_PROPERTY)
                    .build())
            .property(PrimitivePropertyBuilder
                    .create(Datatypes.String, FACILITY_ID_NAME)
                    .label("Facility ID")
                    .description("The ID of the facility")
                    .domainProperty("http://domain.prop/facilityId")
                    .scope(PropertyScope.DIMENSION_PROPERTY)
                    .build())
            .property(PrimitivePropertyBuilder
                    .create(Datatypes.Float, "pressure")
                    .label("Pressure")
                    .description("Measures the current pressure")
                    .domainProperty("http://domain.prop/pressure")
                    .scope(PropertyScope.MEASUREMENT_PROPERTY)
                    .build())
            .format(Formats.jsonFormat())
            .protocol(Protocols.kafka(SourcesConfig.INSTANCE.getKafkaHost(), SourcesConfig.INSTANCE.getKafkaPort(),
                    WildcardTopicBuilder
                            .create("org.streampipes.company.$facilityId.$assemblyLineId.afagor" +
                            ".$machineId.pressure.$sensorId")
                            .addSimpleMapping(TopicParameterType.PLATFORM_IDENTIFIER, FACILITY_ID_NAME)
                            .addSimpleMapping(TopicParameterType.LOCATION_IDENTIFIER, ASSEMBLY_LINE_ID_NAME)
                            .addSimpleMapping(TopicParameterType.SENSOR_IDENTIFIER, SENSOR_ID_NAME)
                            .build()))

            .build();

    // com.company.PRODUCTION_FACILITY_ID.ASSEMBLY_LINE_ID.MACHINE_TYPE.MACHINE_ID.SENSOR_TYPE.SENSOR_ID
//    SpDataStream stream = prepareStream(TOPIC, MessageFormat.Json);
//    stream.setName("Random Number Stream (Wildcard Topic Demo)");
//    stream.setDescription("Random Number Stream Description");
//    stream.setUri(sep.getUri() + "/numberjson");
//
//    return stream;
  }

  @Override
  protected Optional<byte[]> getMessage(long nanoTime, int randomNumber, int counter) {
    try {
      return Optional.of(
              buildJson(nanoTime, randomNumber, counter)
                      .toString()
                      .getBytes());
    } catch (JSONException e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }

  private JSONObject buildJson(long timestamp, int randomNumber, int counter) throws JSONException {
    JSONObject json = new JSONObject();

    json.put("timestamp", timestamp);
    json.put("randomValue", randomNumber);
    json.put("randomString", randomString());
    json.put("count", counter);
    return json;
  }

  public static void main(String[] args) {
    new RandomNumberStreamJson().executeStream();
  }
}
