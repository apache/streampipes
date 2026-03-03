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

package org.apache.streampipes.integration.containers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

public class AzureCosmosDbEmulatorContainer extends GenericContainer<AzureCosmosDbEmulatorContainer> {

  private static final DockerImageName IMAGE_NAME =
      DockerImageName.parse("mcr.microsoft.com/cosmosdb/linux/azure-cosmos-emulator:vnext-preview");
  public static final String DEFAULT_EMULATOR_KEY =
      "C2y6yDjf5/R+ob0N8A7Cgv30VRDJIWEHLM+4QDU5DE2nQ9nDuVTqobD4b8mGGyPMbIZnqyMsEcaGQy67XIw/Jw==";
  public static final int DEFAULT_HOST_HTTPS_PORT = 8081;
  public static final int DEFAULT_HOST_EXPLORER_PORT = 1234;

  private static final int HTTPS_PORT = 8081;
  private static final int EXPLORER_PORT = 1234;
  private static final String HTTPS_PORT_PROPERTY = "streampipes.it.cosmosdb.https.port";
  private static final String EXPLORER_PORT_PROPERTY = "streampipes.it.cosmosdb.explorer.port";

  private final int hostHttpsPort;
  private final int hostExplorerPort;

  public AzureCosmosDbEmulatorContainer() {
    super(IMAGE_NAME);

    this.hostHttpsPort = resolvePort(HTTPS_PORT_PROPERTY, "STREAMPIPES_IT_COSMOSDB_HTTPS_PORT", DEFAULT_HOST_HTTPS_PORT);
    this.hostExplorerPort = resolvePort(
        EXPLORER_PORT_PROPERTY,
        "STREAMPIPES_IT_COSMOSDB_EXPLORER_PORT",
        DEFAULT_HOST_EXPLORER_PORT
    );

    this.withEnv("PROTOCOL", "https");
    this.withEnv("ENABLE_EXPLORER", "true");
    this.addFixedExposedPort(hostHttpsPort, HTTPS_PORT);
    this.addFixedExposedPort(hostExplorerPort, EXPLORER_PORT);
    this.waitingFor(Wait.forListeningPort());
    this.withStartupTimeout(Duration.ofMinutes(5));
  }

  public String getEmulatorEndpoint() {
    return "https://" + getHost() + ":" + hostHttpsPort + "/";
  }

  public String getCertificateEndpoint() {
    return getEmulatorEndpoint() + "_explorer/emulator.pem";
  }

  public String getExplorerEndpoint() {
    return "https://" + getHost() + ":" + hostExplorerPort + "/_explorer/index.html";
  }

  public String getConfiguredEmulatorKey() {
    String propertyValue = System.getProperty("streampipes.it.cosmosdb.key");
    if (propertyValue != null && !propertyValue.isBlank()) {
      return propertyValue;
    }

    String envValue = System.getenv("STREAMPIPES_IT_COSMOSDB_KEY");
    if (envValue != null && !envValue.isBlank()) {
      return envValue;
    }

    return DEFAULT_EMULATOR_KEY;
  }

  private int resolvePort(String propertyName, String envName, int defaultPort) {
    String propertyValue = System.getProperty(propertyName);
    if (propertyValue != null && !propertyValue.isBlank()) {
      return Integer.parseInt(propertyValue);
    }

    String envValue = System.getenv(envName);
    if (envValue != null && !envValue.isBlank()) {
      return Integer.parseInt(envValue);
    }

    return defaultPort;
  }
}
