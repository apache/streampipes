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

package org.apache.streampipes.integration.sinks.ditto;

import org.apache.streampipes.extensions.api.pe.IStreamPipesDataSink;
import org.apache.streampipes.integration.containers.DittoContainerStack;
import org.apache.streampipes.integration.sinks.SinkTesterBase;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventFactory;
import org.apache.streampipes.model.runtime.SchemaInfo;
import org.apache.streampipes.model.runtime.SourceInfo;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.MappingPropertyNary;
import org.apache.streampipes.model.staticproperty.SecretStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.sinks.databases.jvm.ditto.DittoSink;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DittoSinkTester extends SinkTesterBase {

  private static final String FEATURE_ID = "measurements";
  private static final String EXISTING_FEATURE_ID = "existing";

  private final DittoContainerStack ditto;
  private final boolean thingExistsBeforeStart;
  private final String thingId = "org.apache.streampipes:ditto-sink-" + UUID.randomUUID();
  private final ObjectMapper mapper = new ObjectMapper();

  private EventSchema eventSchema;

  /**
   * @param ditto                  running Ditto stack, owned by the caller
   * @param thingExistsBeforeStart whether the thing already exists (with an unrelated feature)
   *                               before the sink starts, or must be created by the sink
   */
  public DittoSinkTester(DittoContainerStack ditto, boolean thingExistsBeforeStart) {
    this.ditto = ditto;
    this.thingExistsBeforeStart = thingExistsBeforeStart;
  }

  @Override
  protected void startSinkService() throws Exception {
    if (thingExistsBeforeStart) {
      String thing = """
          {"features": {"%s": {"properties": {"keep": true}}}}
          """.formatted(EXISTING_FEATURE_ID);
      HttpResponse<String> response = ditto.put("/api/2/things/" + thingId, thing);
      assertEquals(201, response.statusCode(), response.body());
    }
  }

  @Override
  protected IStreamPipesDataSink createSink() {
    return new DittoSink();
  }

  @Override
  protected List<SpDataStream> createInputStreams() {
    this.eventSchema = new EventSchema();
    this.eventSchema.addEventProperty(
        new EventPropertyPrimitive("http://www.w3.org/2001/XMLSchema#string", "sensorId", null, null)
    );
    this.eventSchema.addEventProperty(
        new EventPropertyPrimitive("http://www.w3.org/2001/XMLSchema#float", "temperature", null, null)
    );
    this.eventSchema.addEventProperty(
        new EventPropertyPrimitive("http://www.w3.org/2001/XMLSchema#string", "status", null, null)
    );

    SpDataStream inputStream = new SpDataStream();
    inputStream.setEventSchema(eventSchema);
    return List.of(inputStream);
  }

  @Override
  protected List<StaticProperty> configureStaticProperties(IStreamPipesDataSink sink,
                                                           List<SpDataStream> inputStreams) throws Exception {
    List<StaticProperty> staticProperties = configurator().prepareStaticProperties(sink, inputStreams);

    setText(staticProperties, "dittoApiEndpointKey", ditto.getWebSocketEndpoint());
    setText(staticProperties, "dittoUserKey", DittoContainerStack.USERNAME);
    setText(staticProperties, "dittoThingIdKey", thingId);
    setText(staticProperties, "dittoFeatureIdKey", FEATURE_ID);

    SecretStaticProperty password = configurator().requireProperty(
        staticProperties, "dittoPasswordKey", SecretStaticProperty.class
    );
    password.setValue(DittoContainerStack.PASSWORD);
    password.setEncrypted(false);

    configurator().requireProperty(staticProperties, "selectedFieldsKey", MappingPropertyNary.class)
        .setSelectedProperties(List.of("s0::sensorId", "s0::temperature"));

    return staticProperties;
  }

  @Override
  protected List<Event> createEvents() {
    SourceInfo sourceInfo = new SourceInfo("source-0", "s0");
    SchemaInfo schemaInfo = new SchemaInfo(eventSchema, List.of());

    return List.<Map<String, Object>>of(
            Map.of("sensorId", "sensor-1", "temperature", 21.5, "status", "ok"),
            Map.of("sensorId", "sensor-1", "temperature", 22.75, "status", "warning")
        )
        .stream()
        .map(values -> EventFactory.fromMap(values, sourceInfo, schemaInfo))
        .toList();
  }

  @Override
  protected void validate(List<Event> events) throws Exception {
    HttpResponse<String> response = ditto.get("/api/2/things/" + thingId);
    assertEquals(200, response.statusCode(), response.body());

    JsonNode features = mapper.readTree(response.body()).path("features");
    JsonNode properties = features.path(FEATURE_ID).path("properties");

    // the sink writes every selected field as a string property; the last event wins
    assertEquals("sensor-1", properties.path("sensorId").asText());
    assertEquals("22.75", properties.path("temperature").asText());
    assertFalse(properties.has("status"), "Unselected field must not be written: " + properties);

    if (thingExistsBeforeStart) {
      assertTrue(
          features.path(EXISTING_FEATURE_ID).path("properties").path("keep").asBoolean(),
          "Existing feature must be preserved: " + features
      );
    }
  }

  private void setText(List<StaticProperty> staticProperties, String internalName, String value) {
    configurator().requireProperty(staticProperties, internalName, FreeTextStaticProperty.class).setValue(value);
  }
}
