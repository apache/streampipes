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

package org.apache.streampipes.extensions.connectors.camel.kamelet.transform;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataformat.JsonDataFormatDefinition;
import org.apache.streampipes.extensions.connectors.camel.kamelet.message.KameletEventMessageMapper;
import org.apache.streampipes.extensions.connectors.camel.kamelet.message.KameletHeaderMapping;
import org.apache.streampipes.extensions.connectors.camel.kamelet.message.KameletMessageMapping;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.field.PrimitiveField;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KameletTransformRouteLoaderTest {

  private final KameletTransformRouteLoader loader = new KameletTransformRouteLoader();

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"  ", "[]"})
  void forwardsWithoutTransform(String yaml) throws Exception {
    try (var context = context()) {
      loader.addRoute(context, "transform", "direct:input", "direct:output", yaml);
      context.start();
      try (var producer = context.createProducerTemplate()) {
        assertEquals("original", producer.requestBody("direct:input", "original"));
      }
    }
  }

  @Test
  void appliesDataOnlyStepsAndDeliversToConfiguredDestination() throws Exception {
    try (var context = context()) {
      loader.addRoute(context, "transform", "direct:input", "direct:output", """
          - setHeader:
              name: example
              simple: "${body[myField]}"
          - set-header:
              name: literal
              constant: "{{env:PRIVATE}} ${bean:unused}"
          - setHeader:
              name: temporary
              constant: "remove me"
          - remove-header:
              name: temporary
          - set-body:
              simple: "${body[myField]}"
          """);
      context.start();
      try (var producer = context.createProducerTemplate()) {
        var result = producer.request("direct:input", exchange ->
            exchange.getMessage().setBody(Map.of("myField", "value")));
        assertNull(result.getException());
        assertEquals("value", result.getMessage().getBody());
        assertEquals("value", result.getMessage().getHeader("example"));
        assertEquals("{{env:PRIVATE}} ${bean:unused}", result.getMessage().getHeader("literal"));
        assertNull(result.getMessage().getHeader("temporary"));
        assertEquals(true, result.getMessage().getHeader("delivered"));
      }
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "- setBody: {constant: 'literal'}",
      "- setBody: {simple: '${body}'}"
  })
  void supportsBodyAssignment(String yaml) throws Exception {
    try (var context = context()) {
      loader.addRoute(context, "transform", "direct:input", "direct:output", yaml);
      context.start();
      try (var producer = context.createProducerTemplate()) {
        assertEquals(yaml.contains("constant") ? "literal" : "original",
            producer.requestBody("direct:input", "original"));
      }
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "- setBody: {simple: '${type:java.lang.System}'}",
      "- setHeader: {name: value, simple: '${bean:untrusted}'}",
      "- setBody: {simple: '${body.getClass()}'}",
      "- setBody: {simple: '${body[myField].getClass()}'}",
      "- setBody: {simple: '{{untrusted}}'}",
      "- setBody: {simple: '$simple{body}'}",
      "- setBody: {groovy: 'return 1'}",
      "- setBody: {expression: {simple: '${body}'}}",
      "- setBody: {constant: 'value', simple: '${body}'}",
      "- setBody: {constant: {value: nested}}",
      "- setBody: {constant: 42}",
      "- setHeader: {name: CamelHttpUri, constant: 'unused'}",
      "- setHeader: {name: cAmElControl, constant: 'unused'}",
      "- setHeader: {name: '{{name}}', constant: 'unused'}",
      "- bean: {ref: untrusted}",
      "- process: {ref: untrusted}",
      "- to: {uri: 'bean:untrusted'}",
      "- to: {uri: 'class:java.lang.String'}",
      "- to: {uri: 'direct:other'}",
      "- toD: '${body}'",
      "- script: {groovy: 'return 1'}",
      "- choice: {when: [{simple: 'true', steps: [{to: 'direct:other'}]}]}",
      "- route: {from: 'direct:other'}",
      "- beans: []",
      "- unknown: {}",
      "- setBody: {constant: one, constant: two}",
      "- &step setBody: {constant: value}\n- *step",
      "- setBody: {constant: &value literal}\n- setBody: {constant: *value}",
      "- setBody: {constant: !!java.lang.String value}",
      "- setBody: {constant: value}\n---\n- setBody: {constant: other}",
      "setBody: {constant: value}",
      "- setBody: {constant: value}\n  removeHeader: {name: other}",
      "- setBody: [",
      "- null"
  })
  void rejectsUnsupportedConfigurationBeforeInstallingAnyRoute(String yaml) throws Exception {
    try (var context = new DefaultCamelContext()) {
      // Even a valid prefix must not be installed when a later step is invalid.
      assertThrows(SpRuntimeException.class, () -> loader.addRoute(context, "transform",
          "direct:input", "direct:output", "- setBody: {constant: valid}\n" + yaml));
      assertTrue(context.getRouteDefinitions().isEmpty());
      assertTrue(context.getEndpoints().isEmpty());
    }
  }

  @Test
  void rejectsExcessiveSizeStepCountAndNesting() throws Exception {
    for (String yaml : new String[] {
        "- setBody: {constant: '" + "x".repeat(16_384) + "'}",
        "- setBody: {constant: value}\n".repeat(65),
        "[".repeat(100) + "]".repeat(100)
    }) {
      try (var context = new DefaultCamelContext()) {
        assertThrows(SpRuntimeException.class, () -> loader.addRoute(context, "transform",
            "direct:input", "direct:output", yaml));
        assertTrue(context.getRouteDefinitions().isEmpty());
      }
    }
  }

  @ParameterizedTest
  @EnumSource(KameletMessageMapping.PayloadMode.class)
  void preservesExistingMessageMapping(KameletMessageMapping.PayloadMode mode) throws Exception {
    var event = new Event();
    event.addField(new PrimitiveField("myField", "myField", "value"));
    String selector = event.getFields().keySet().iterator().next();
    var mapping = new KameletMessageMapping(mode, selector,
        List.of(new KameletHeaderMapping("mapped", selector)), null);
    var message = new KameletEventMessageMapper().mapEvent(event, mapping, new JsonDataFormatDefinition());
    try (var context = context()) {
      loader.addRoute(context, "transform", "direct:input", "direct:output", null);
      context.start();
      try (var producer = context.createProducerTemplate()) {
        var result = producer.request("direct:input", exchange -> {
          exchange.getMessage().setBody(message.body());
          exchange.getMessage().setHeaders(message.headers());
        });
        assertNull(result.getException());
        assertEquals(message.body(), result.getMessage().getBody());
        assertEquals("value", result.getMessage().getHeader("mapped"));
        assertEquals(message.headers().get(KameletEventMessageMapper.EVENT_JSON_HEADER),
            result.getMessage().getHeader(KameletEventMessageMapper.EVENT_JSON_HEADER));
        assertEquals(true, result.getMessage().getHeader("delivered"));
      }
    }
  }

  @Test
  void deliversThroughAnInstalledKameletTemplate() throws Exception {
    try (var context = context()) {
      context.addRoutes(new RouteBuilder() {
        @Override
        public void configure() {
          routeTemplate("local-sink").from("kamelet:source").to("direct:output");
        }
      });
      loader.addRoute(context, "transform", "direct:input", "kamelet:local-sink", """
          - setBody:
              simple: "${body[myField]}"
          """);
      context.start();
      try (var producer = context.createProducerTemplate()) {
        var result = producer.request("direct:input", exchange ->
            exchange.getMessage().setBody(Map.of("myField", "value")));
        assertNull(result.getException());
        assertEquals("value", result.getMessage().getBody());
        assertEquals(true, result.getMessage().getHeader("delivered"));
      }
    }
  }

  @Test
  void reportsInvalidYamlWithoutEchoingConfiguration() throws Exception {
    try (var context = new DefaultCamelContext()) {
      var error = assertThrows(SpRuntimeException.class, () -> loader.addRoute(context, "transform",
          "direct:input", "direct:output", "- setBody: {constant: private-value, constant: duplicate}"));
      assertTrue(error.getMessage().contains("setBody"));
      assertTrue(!error.getMessage().contains("private-value"));
    }
  }

  private DefaultCamelContext context() throws Exception {
    var context = new DefaultCamelContext();
    context.addRoutes(new RouteBuilder() {
      @Override
      public void configure() {
        from("direct:output").process(exchange -> exchange.getMessage().setHeader("delivered", true));
      }
    });
    return context;
  }
}
