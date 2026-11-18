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

import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.api.lowlevel.Parse;
import org.snakeyaml.engine.v2.exceptions.YamlEngineException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Interprets a deliberately small data-only subset of Camel YAML.
 * Never pass user configuration to Camel's DSL or expression loaders: those execute trusted code.
 * The entry and destination URIs must be supplied by the installed extension, not by this fragment.
 */
public class KameletTransformRouteLoader {

  private static final int MAX_LENGTH = 16_384;
  private static final int MAX_STEPS = 64;
  private static final LoadSettings SETTINGS = LoadSettings.builder()
      .setAllowDuplicateKeys(false)
      .setMaxAliasesForCollections(0)
      .setCodePointLimit(MAX_LENGTH)
      .build();
  private static final Pattern FIELD_EXPRESSION = Pattern.compile("\\$\\{body\\[([a-zA-Z0-9_-]+)]}");
  private static final Pattern HEADER_NAME = Pattern.compile("[a-zA-Z0-9_-]{1,128}");

  public void addRoute(CamelContext camelContext,
                       String routeId,
                       String fromUri,
                       String toUri,
                       String transformStepsYaml) throws Exception {
    // Validate the complete fragment before registering any route or running any processor.
    List<Processor> steps = parseSteps(transformStepsYaml);
    camelContext.addRoutes(new RouteBuilder() {
      @Override
      public void configure() {
        from(fromUri)
            .routeId(routeId)
            .process(exchange -> {
              for (Processor step : steps) {
                step.process(exchange);
              }
            })
            .to(toUri);
      }
    });
  }

  private List<Processor> parseSteps(String yaml) {
    if (yaml == null || yaml.isBlank()) {
      return List.of();
    }
    if (yaml.length() > MAX_LENGTH) {
      throw invalid();
    }
    Object parsed;
    try {
      // Bound nesting before constructing collections, and disallow all aliases (including scalars).
      int depth = 0;
      for (var event : new Parse(SETTINGS).parseString(yaml)) {
        switch (event.getEventId()) {
          case MappingStart, SequenceStart -> {
            if (++depth > 4) {
              throw invalid();
            }
          }
          case MappingEnd, SequenceEnd -> depth--;
          case Alias -> throw invalid();
          default -> { }
        }
      }
      parsed = new Load(SETTINGS).loadFromString(yaml);
    } catch (YamlEngineException e) {
      // Do not expose configuration values or parser snippets in errors.
      throw invalid();
    }
    if (!(parsed instanceof List<?> list) || list.size() > MAX_STEPS) {
      throw invalid();
    }
    List<Processor> steps = new ArrayList<>();
    for (Object item : list) {
      if (!(item instanceof Map<?, ?> step) || step.size() != 1) {
        throw invalid();
      }
      var entry = step.entrySet().iterator().next();
      if (!(entry.getValue() instanceof Map<?, ?> options)) {
        throw invalid();
      }
      switch (String.valueOf(entry.getKey())) {
        case "setBody", "set-body" -> {
          Function<Exchange, Object> value = expression(options, false);
          steps.add(exchange -> exchange.getMessage().setBody(value.apply(exchange)));
        }
        case "setHeader", "set-header" -> {
          String name = headerName(options);
          Function<Exchange, Object> value = expression(options, true);
          steps.add(exchange -> exchange.getMessage().setHeader(name, value.apply(exchange)));
        }
        case "removeHeader", "remove-header" -> {
          if (!options.keySet().equals(Set.of("name"))) {
            throw invalid();
          }
          String name = headerName(options);
          steps.add(exchange -> exchange.getMessage().removeHeader(name));
        }
        default -> throw invalid();
      }
    }
    return List.copyOf(steps);
  }

  private Function<Exchange, Object> expression(Map<?, ?> options, boolean header) {
    String language = options.containsKey("constant") ? "constant" : "simple";
    Set<String> keys = header ? Set.of("name", language) : Set.of(language);
    if (!options.keySet().equals(keys) || !(options.get(language) instanceof String value)) {
      throw invalid();
    }
    if (language.equals("constant")) {
      return exchange -> value;
    }
    // Compatibility with the shipped example, without Simple/OGNL, property or bean resolution.
    if (value.equals("${body}")) {
      return exchange -> exchange.getMessage().getBody();
    }
    var matcher = FIELD_EXPRESSION.matcher(value);
    if (!matcher.matches()) {
      throw invalid();
    }
    String field = matcher.group(1);
    return exchange -> exchange.getMessage().getBody() instanceof Map<?, ?> body ? body.get(field) : null;
  }

  private String headerName(Map<?, ?> options) {
    if (!(options.get("name") instanceof String name) || !HEADER_NAME.matcher(name).matches()
        || name.regionMatches(true, 0, "Camel", 0, 5)) {
      // Camel-prefixed headers can override endpoint operations, destinations and other control options.
      throw invalid();
    }
    return name;
  }

  private SpRuntimeException invalid() {
    return new SpRuntimeException(new SpConfigurationException(
        "Transform YAML supports only setBody, setHeader and removeHeader with literal string constants "
            + "or simple ${body}/${body[field]} references. Camel control headers, other DSL constructs, "
            + "aliases and oversized fragments are not allowed."
    ));
  }
}
