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

package org.apache.streampipes.extensions.connectors.camel.kamelet.discovery;

import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.extensions.connectors.camel.kamelet.assets.KameletIconDecoder;
import org.apache.streampipes.extensions.connectors.camel.kamelet.filter.KameletTemplateFilter;
import org.apache.streampipes.extensions.connectors.camel.kamelet.model.KameletPropertyDefinition;
import org.apache.streampipes.extensions.connectors.camel.kamelet.model.KameletTemplate;
import org.apache.streampipes.sdk.utils.Datatypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

import java.net.JarURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class KameletTemplateDiscovery {

  private static final Logger LOG = LoggerFactory.getLogger(KameletTemplateDiscovery.class);
  private static final Load YAML_LOADER = new Load(LoadSettings.builder().build());

  private final KameletIconDecoder iconDecoder;

  public KameletTemplateDiscovery() {
    this.iconDecoder = new KameletIconDecoder();
  }

  public Map<String, KameletTemplate> discoverTemplates() throws SpConfigurationException {
    return discoverTemplates(KameletTemplateFilter.includeAll());
  }

  public Map<String, KameletTemplate> discoverTemplates(KameletTemplateFilter filter) throws SpConfigurationException {
    try {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      Enumeration<URL> resources = classLoader.getResources("kamelets");
      List<KameletTemplate> templates = new ArrayList<>();
      Set<String> seenNames = new HashSet<>();

      while (resources.hasMoreElements()) {
        URL url = resources.nextElement();
        if ("file".equals(url.getProtocol())) {
          collectFromFileDirectory(url, templates, seenNames);
        } else if ("jar".equals(url.getProtocol())) {
          collectFromJar(url, templates, seenNames);
        } else {
          LOG.debug("Skipping unsupported kamelets resource URL protocol {} ({})", url.getProtocol(), url);
        }
      }

      return templates.stream()
          .filter(filter::test)
          .sorted(Comparator.comparing(KameletTemplate::displayName))
          .collect(Collectors.toMap(
              KameletTemplate::name,
              template -> template,
              (first, second) -> first,
              LinkedHashMap::new
          ));
    } catch (Exception e) {
      throw new SpConfigurationException("Could not discover registered Kamelet templates", e);
    }
  }

  private void collectFromFileDirectory(URL directoryUrl,
                                        List<KameletTemplate> templates,
                                        Set<String> seenNames) throws Exception {
    Path directory = Path.of(directoryUrl.toURI());
    if (!Files.isDirectory(directory)) {
      return;
    }

    try (var stream = Files.walk(directory, 1)) {
      for (Path path : stream.filter(Files::isRegularFile).toList()) {
        if (path.getFileName().toString().endsWith(".kamelet.yaml")) {
          addTemplateIfSupported(Files.readString(path), templates, seenNames);
        }
      }
    }
  }

  private void collectFromJar(URL jarDirUrl,
                              List<KameletTemplate> templates,
                              Set<String> seenNames) throws Exception {
    JarURLConnection connection = (JarURLConnection) jarDirUrl.openConnection();
    String prefix = connection.getEntryName() == null ? "kamelets/" : ensureTrailingSlash(connection.getEntryName());

    try (JarFile jarFile = connection.getJarFile()) {
      var entries = jarFile.entries();
      while (entries.hasMoreElements()) {
        JarEntry entry = entries.nextElement();
        String name = entry.getName();
        if (entry.isDirectory() || !name.startsWith(prefix) || !name.endsWith(".kamelet.yaml")) {
          continue;
        }
        if (name.substring(prefix.length()).contains("/")) {
          continue;
        }

        String content = new String(jarFile.getInputStream(entry).readAllBytes(), StandardCharsets.UTF_8);
        addTemplateIfSupported(content, templates, seenNames);
      }
    }
  }

  private void addTemplateIfSupported(String yamlContent,
                                      List<KameletTemplate> templates,
                                      Set<String> seenNames) {
    try {
      Object parsed = YAML_LOADER.loadFromString(yamlContent);
      if (!(parsed instanceof Map<?, ?> root)) {
        return;
      }

      KameletTemplate template = parseTemplate(root, yamlContent);
      if (template != null && seenNames.add(template.name())) {
        templates.add(template);
      }
    } catch (Exception e) {
      LOG.debug("Skipping unreadable Kamelet YAML during template discovery", e);
    }
  }

  private KameletTemplate parseTemplate(Map<?, ?> root,
                                        String yamlContent) {
    Map<String, Object> metadata = asStringObjectMap(root.get("metadata"));
    Map<String, Object> annotations = asStringObjectMap(metadata.get("annotations"));
    Map<String, Object> spec = asStringObjectMap(root.get("spec"));
    Map<String, Object> definition = asStringObjectMap(spec.get("definition"));
    Map<String, Object> template = asStringObjectMap(spec.get("template"));
    Map<String, Object> from = asStringObjectMap(template.get("from"));


    String name = trimmedString(metadata.get("name"));
    if (name == null || name.isEmpty() || !isSinkKamelet(name, from)) {
      return null;
    }

    String displayName = firstNonBlank(trimmedString(definition.get("title")), name);
    String description = firstNonBlank(trimmedString(definition.get("description")), "");
    String group = firstNonBlank(trimmedString(annotations.get("camel.apache.org/kamelet.group")), "");
    String toUriTemplate = getUriTemplateString(from);
    List<KameletPropertyDefinition> properties = parseProperties(definition);

    return new KameletTemplate(
        displayName,
        description,
        group,
        name,
        "kamelet:" + name + "/sink",
        toUriTemplate,
        properties,
        sanitizeYamlSpec(yamlContent).strip(),
        decodeEmbeddedIcon(annotations)
    );
  }

  private String getUriTemplateString(Map<String, Object> from) {
    return findToUri(asObjectList(from.get("steps"))).orElse("");
  }

  private java.util.Optional<String> findToUri(List<Object> steps) {
    for (Object rawStep : steps) {
      Map<String, Object> step = asStringObjectMap(rawStep);
      if (step.isEmpty()) {
        continue;
      }

      String directToUri = extractToUri(step);
      if (directToUri != null) {
        return java.util.Optional.of(directToUri);
      }

      java.util.Optional<String> nestedToUri = findToUriInNestedBlocks(step);
      if (nestedToUri.isPresent()) {
        return nestedToUri;
      }
    }

    return java.util.Optional.empty();
  }

  private String extractToUri(Map<String, Object> step) {
    Map<String, Object> to = asStringObjectMap(step.get("to"));
    String toUri = trimmedString(to.get("uri"));
    if (toUri != null && !toUri.isBlank()) {
      return toUri;
    }

    return null;
  }

  private java.util.Optional<String> findToUriInNestedBlocks(Map<String, Object> step) {
    for (Object value : step.values()) {
      java.util.Optional<String> nestedToUri = findToUriInValue(value);
      if (nestedToUri.isPresent()) {
        return nestedToUri;
      }
    }

    return java.util.Optional.empty();
  }

  private java.util.Optional<String> findToUriInValue(Object value) {
    if (value instanceof List<?> list) {
      return findToUri(new ArrayList<>(list));
    }

    if (value instanceof Map<?, ?> rawMap) {
      Map<String, Object> map = asStringObjectMap(rawMap);
      java.util.Optional<String> fromSteps = findToUri(asObjectList(map.get("steps")));
      if (fromSteps.isPresent()) {
        return fromSteps;
      }

      for (Object nestedValue : map.values()) {
        java.util.Optional<String> nested = findToUriInValue(nestedValue);
        if (nested.isPresent()) {
          return nested;
        }
      }
    }

    return java.util.Optional.empty();
  }


  private String sanitizeYamlSpec(String yamlContent) {
    String[] lines = removeLeadingCommentBlock(yamlContent)
        .replace("\r\n", "\n")
        .split("\n", -1);
    StringBuilder sanitized = new StringBuilder();
    boolean skipping = false;
    int skipIndent = -1;

    for (String line : lines) {
      if (!skipping && isKameletIconLine(line)) {
        skipping = true;
        skipIndent = indentation(line);
        continue;
      }

      if (skipping) {
        if (line.isBlank()) {
          continue;
        }

        int currentIndent = indentation(line);
        if (currentIndent > skipIndent) {
          continue;
        }

        skipping = false;
      }

      sanitized.append(line).append("\n");
    }

    return sanitized.toString();
  }

  private String removeLeadingCommentBlock(String yamlContent) {
    String normalized = yamlContent.replace("\r\n", "\n");
    String[] lines = normalized.split("\n", -1);
    int index = 0;

    while (index < lines.length && (lines[index].isBlank() || lines[index].stripLeading().startsWith("#"))) {
      index++;
    }

    return String.join("\n", java.util.Arrays.copyOfRange(lines, index, lines.length));
  }

  private boolean isKameletIconLine(String line) {
    String trimmed = line.stripLeading();
    return trimmed.startsWith("camel.apache.org/kamelet.icon:");
  }

  private int indentation(String line) {
    int indent = 0;
    while (indent < line.length() && Character.isWhitespace(line.charAt(indent))) {
      indent++;
    }
    return indent;
  }

  private byte[] decodeEmbeddedIcon(Map<String, Object> annotations) {
    String base64Icon = trimmedString(annotations.get("camel.apache.org/kamelet.icon"));
    return iconDecoder.decode(base64Icon);
  }

  private List<KameletPropertyDefinition> parseProperties(Map<String, Object> definition) {
    Map<String, Object> properties = asStringObjectMap(definition.get("properties"));
    Set<String> required = extractRequired(definition.get("required"));
    List<KameletPropertyDefinition> result = new ArrayList<>();

    properties.forEach((name, rawSpec) -> {
      Map<String, Object> spec = asStringObjectMap(rawSpec);
      List<String> allowedValues = determineAllowedValues(spec);
      result.add(new KameletPropertyDefinition(
          name,
          firstNonBlank(trimmedString(spec.get("title")), name),
          firstNonBlank(trimmedString(spec.get("description")), ""),
          determineInputType(name, spec, allowedValues),
          determineDatatype(spec),
          required.contains(name),
          allowedValues,
          defaultValue(spec.get("default"))
      ));
    });

    return result;
  }

  private KameletPropertyDefinition.PropertyInputType determineInputType(String propertyName,
                                                                         Map<String, Object> spec,
                                                                         List<String> allowedValues) {
    if (!allowedValues.isEmpty()) {
      return KameletPropertyDefinition.PropertyInputType.ONE_OF;
    }
    if (isSecretProperty(propertyName, spec)) {
      return KameletPropertyDefinition.PropertyInputType.SECRET;
    }
    return KameletPropertyDefinition.PropertyInputType.TEXT;
  }

  private Datatypes determineDatatype(Map<String, Object> spec) {
    String type = trimmedString(spec.get("type"));
    if ("boolean".equals(type)) {
      return Datatypes.Boolean;
    } else if ("integer".equals(type)) {
      return Datatypes.Integer;
    } else if ("number".equals(type)) {
      return Datatypes.Double;
    } else {
      return Datatypes.String;
    }
  }

  private boolean isSecretProperty(String propertyName, Map<String, Object> spec) {
    String format = trimmedString(spec.get("format"));
    if ("password".equalsIgnoreCase(format)) {
      return true;
    }

    Object descriptors = spec.get("x-descriptors");
    if (descriptors instanceof List<?> list) {
      boolean fromDescriptors = list.stream()
          .map(String::valueOf)
          .map(String::toLowerCase)
          .anyMatch(value -> value.contains("password") || value.contains("secret")
              || value.contains("credential") || value.contains("token"));
      if (fromDescriptors) {
        return true;
      }
    }

    String loweredName = propertyName.toLowerCase();
    return loweredName.contains("password")
        || loweredName.contains("secret")
        || loweredName.contains("token")
        || loweredName.contains("apikey")
        || loweredName.contains("api-key");
  }

  private boolean isSinkKamelet(String name, Map<String, Object> from) {
    String fromUri = trimmedString(from.get("uri"));
    return "kamelet:source".equals(fromUri) || name.endsWith("-sink");
  }

  private List<String> determineAllowedValues(Map<String, Object> spec) {
    List<String> enumValues = extractAllowedValues(spec.get("enum"));
    if (!enumValues.isEmpty()) {
      return enumValues;
    }

    String type = trimmedString(spec.get("type"));
    if ("boolean".equals(type)) {
      return List.of(Boolean.TRUE.toString(), Boolean.FALSE.toString());
    }

    return List.of();
  }

  private List<String> extractAllowedValues(Object value) {
    if (value instanceof List<?> list) {
      return list.stream().map(String::valueOf).toList();
    }
    return List.of();
  }

  private Set<String> extractRequired(Object value) {
    if (value instanceof List<?> list) {
      return list.stream().map(String::valueOf).collect(Collectors.toSet());
    }
    return Set.of();
  }

  private String defaultValue(Object value) {
    return value == null ? null : String.valueOf(value);
  }

  private Map<String, Object> asStringObjectMap(Object value) {
    if (value instanceof Map<?, ?> raw) {
      LinkedHashMap<String, Object> result = new LinkedHashMap<>();
      raw.forEach((k, v) -> result.put(String.valueOf(k), v));
      return result;
    }
    return Map.of();
  }

  private List<Object> asObjectList(Object value) {
    if (value instanceof List<?> list) {
      return new ArrayList<>(list);
    }
    return List.of();
  }

  private String trimmedString(Object value) {
    return value == null ? null : String.valueOf(value).trim();
  }

  private String firstNonBlank(String... values) {
    for (String value : values) {
      if (value != null && !value.isBlank()) {
        return value;
      }
    }
    return "";
  }

  private String ensureTrailingSlash(String prefix) {
    return prefix.endsWith("/") ? prefix : prefix + "/";
  }
}
