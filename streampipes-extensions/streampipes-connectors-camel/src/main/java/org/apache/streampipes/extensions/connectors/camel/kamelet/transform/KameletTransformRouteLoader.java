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
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dsl.yaml.YamlRoutesBuilderLoader;
import org.apache.camel.spi.Resource;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KameletTransformRouteLoader {

  private static final Load YAML_LOADER = new Load(LoadSettings.builder().build());
  private static final Set<String> FORBIDDEN_KEYS = Set.of(
      "route",
      "from",
      "templatedRoute",
      "routeTemplate",
      "routeConfiguration",
      "routeConfigurations",
      "rest",
      "beans"
  );

  public void addRoute(CamelContext camelContext,
                       String routeId,
                       String fromUri,
                       String toUri,
                       String transformStepsYaml) throws Exception {
    if (transformStepsYaml == null || transformStepsYaml.isBlank()) {
      camelContext.addRoutes(new RouteBuilder() {
        @Override
        public void configure() {
          from(fromUri)
              .routeId(routeId)
              .to(toUri);
        }
      });
      return;
    }

    validateSteps(transformStepsYaml);

    YamlRoutesBuilderLoader loader = new YamlRoutesBuilderLoader();
    loader.setCamelContext(camelContext);

    camelContext.addRoutes(loader.doLoadRouteBuilder(new InMemoryResource(
        "in-memory:" + routeId + ".yaml",
        buildRouteYaml(routeId, fromUri, toUri, transformStepsYaml)
    )));
  }

  private void validateSteps(String transformStepsYaml) {
    Object parsed = YAML_LOADER.loadFromString(transformStepsYaml);
    if (!(parsed instanceof List<?> list)) {
      throw new SpRuntimeException(new SpConfigurationException(
          "Advanced transform steps must be a YAML list of Camel steps."
      ));
    }

    validateNode(list);
  }

  private void validateNode(Object node) {
    if (node instanceof List<?> list) {
      list.forEach(this::validateNode);
    } else if (node instanceof Map<?, ?> map) {
      for (Map.Entry<?, ?> entry : map.entrySet()) {
        String key = String.valueOf(entry.getKey());
        if (FORBIDDEN_KEYS.contains(key)) {
          throw new SpRuntimeException(new SpConfigurationException(
              "Advanced transform steps may not declare Camel routes, beans or from clauses."
          ));
        }
        validateNode(entry.getValue());
      }
    }
  }

  private String buildRouteYaml(String routeId,
                                String fromUri,
                                String toUri,
                                String transformStepsYaml) {
    return """
        - route:
            id: '%s'
            from:
              uri: '%s'
              steps:
        %s
                - to:
                    uri: '%s'
        """.formatted(
        escapeYaml(routeId),
        escapeYaml(fromUri),
        indentSteps(transformStepsYaml),
        escapeYaml(toUri)
    );
  }

  private String indentSteps(String transformStepsYaml) {
    StringBuilder builder = new StringBuilder();
    String[] lines = transformStepsYaml.replace("\r\n", "\n").split("\n", -1);

    for (String line : lines) {
      if (line.isBlank()) {
        builder.append('\n');
      } else {
        builder.append("        ").append(line).append('\n');
      }
    }

    return builder.toString();
  }

  private String escapeYaml(String value) {
    return value.replace("'", "''");
  }

  private static final class InMemoryResource implements Resource {

    private final String location;
    private final byte[] content;

    private InMemoryResource(String location,
                             String content) {
      this.location = location;
      this.content = content.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String getScheme() {
      return "in-memory";
    }

    @Override
    public String getLocation() {
      return location;
    }

    @Override
    public boolean exists() {
      return true;
    }

    @Override
    public InputStream getInputStream() {
      return new ByteArrayInputStream(content);
    }
  }
}
