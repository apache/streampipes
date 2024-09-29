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
package org.apache.streampipes.extensions.connectors.plc.adapter.generic.assets;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.plc4x.java.api.PlcDriver;
import org.apache.plc4x.java.api.metadata.Option;
import org.apache.plc4x.java.api.metadata.OptionMetadata;

public class DocumentationGenerator {

  private static final String DRIVER_NAME = "DRIVER_NAME";
  private static final String SUPPORTED_TRANSPORTS = "SUPPORTED_TRANSPORTS";
  private static final String TRANSPORT_METADATA_REQUIRED = "TRANSPORT_METADATA_REQUIRED";

  private static final String TRANSPORT_METADATA_ADVANCED = "TRANSPORT_METADATA_ADVANCED";

  private static final String PROTOCOL_METADATA_REQUIRED = "PROTOCOL_METADATA_REQUIRED";

  private static final String PROTOCOL_METADATA_ADVANCED = "PROTOCOL_METADATA_ADVANCED";

  private final PlcDriver driver;
  private String docsTemplate;

  public DocumentationGenerator(PlcDriver driver, String docsTemplate) {
    this.driver = driver;
    this.docsTemplate = docsTemplate;
  }

  public byte[] generateDocumentation() {
    var optionMetadata = driver.getMetadata().getProtocolConfigurationOptionMetadata();
    var protocolMetadataRequired = getMetadata(optionMetadata, OptionMetadata::getRequiredOptions);
    var protocolMetadataAdvanced = getMetadata(optionMetadata, OptionMetadata::getOptions);

    docsTemplate = docsTemplate.replaceAll(DRIVER_NAME, driver.getProtocolName())
            .replaceAll(SUPPORTED_TRANSPORTS, toMarkdownList(getSupportedTransports()))
            .replaceAll(PROTOCOL_METADATA_REQUIRED, toMarkdownList(protocolMetadataRequired))
            .replaceAll(PROTOCOL_METADATA_ADVANCED, toMarkdownList(protocolMetadataAdvanced));

    driver.getMetadata().getSupportedTransportCodes().forEach(tc -> {
      var transportMetadata = driver.getMetadata().getTransportConfigurationOptionMetadata(tc);
      var transportMetadataRequired = getMetadata(transportMetadata, OptionMetadata::getRequiredOptions);
      var transportMetadataAdvanced = getMetadata(transportMetadata, OptionMetadata::getOptions);
      docsTemplate = replaceTransportMetadata(docsTemplate, TRANSPORT_METADATA_REQUIRED, tc, transportMetadataRequired);
      docsTemplate = replaceTransportMetadata(docsTemplate, TRANSPORT_METADATA_ADVANCED, tc, transportMetadataAdvanced);
    });

    return docsTemplate.getBytes(StandardCharsets.UTF_8);
  }

  private String replaceTransportMetadata(String docsTemplate, String placeholder, String transportCode,
          List<String> metadata) {
    return docsTemplate.replaceAll(placeholder, String.format("**%s**\n\n%s", transportCode, toMarkdownList(metadata)));
  }

  private List<String> getSupportedTransports() {
    return driver.getMetadata().getSupportedTransportCodes();
  }

  private List<String> getMetadata(Optional<OptionMetadata> optionMetadata,
          Function<OptionMetadata, List<Option>> metadataFn) {
    return optionMetadata.map(om -> metadataFn.apply(om).stream()
            .map(meta -> String.format("%s: %s (%s)", meta.getKey(), meta.getDescription(), meta.getType().toString()))
            .toList()).orElseGet(List::of);
  }

  private String toMarkdownList(List<String> metadata) {
    return metadata.stream().map(s -> "* " + s).collect(Collectors.joining("\n"));
  }
}
