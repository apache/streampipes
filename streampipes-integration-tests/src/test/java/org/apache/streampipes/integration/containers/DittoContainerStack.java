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
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Minimal Eclipse Ditto deployment for integration tests: MongoDB, the policies, things and gateway
 * services, and an nginx front door that performs HTTP basic authentication, mirroring Ditto's
 * reference docker-compose setup (deployment/docker in the Ditto repository).
 */
public class DittoContainerStack implements AutoCloseable {

  public static final String DITTO_VERSION = "3.9.8";
  public static final String USERNAME = "ditto";
  public static final String PASSWORD = "ditto";

  private static final String CLUSTER_ALIAS = "ditto-cluster";
  private static final int STATUS_PORT = 7626;
  private static final int NGINX_PORT = 80;
  private static final Duration STARTUP_TIMEOUT = Duration.ofMinutes(4);
  private static final Duration API_READY_TIMEOUT = Duration.ofMinutes(3);

  private static final String JAVA_TOOL_OPTIONS = String.join(
      " ",
      "-Xmx384m",
      "-Xss512k",
      "-XX:ActiveProcessorCount=2",
      "-XX:+ExitOnOutOfMemoryError",
      "-Dpekko.coordinated-shutdown.exit-jvm=on",
      "-Dpekko.cluster.shutdown-after-unsuccessful-join-seed-nodes=180s",
      "-Dpekko.cluster.downing-provider-class="
  );

  private static final String NGINX_CONF = """
      events {
        worker_connections 1024;
      }
      http {
        server {
          listen 80;

          location /api {
            auth_basic "Authentication required";
            auth_basic_user_file nginx.htpasswd;
            proxy_pass http://gateway:8080;
            proxy_http_version 1.1;
            proxy_set_header Host $http_host;
            proxy_set_header x-ditto-pre-authenticated "nginx:${remote_user}";
          }

          location /ws {
            auth_basic "Authentication required";
            auth_basic_user_file nginx.htpasswd;
            proxy_pass http://gateway:8080;
            proxy_http_version 1.1;
            proxy_set_header Host $http_host;
            proxy_set_header x-ditto-pre-authenticated "nginx:${remote_user}";
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection "upgrade";
            proxy_read_timeout 1d;
            proxy_send_timeout 1d;
          }
        }
      }
      """;

  private final Network network = Network.newNetwork();
  private final HttpClient httpClient = HttpClient.newBuilder()
      .connectTimeout(Duration.ofSeconds(10))
      .build();

  private final GenericContainer<?> mongodb;
  private final GenericContainer<?> policies;
  private final GenericContainer<?> things;
  private final GenericContainer<?> gateway;
  private final GenericContainer<?> nginx;

  public DittoContainerStack() {
    this.mongodb = new GenericContainer<>(DockerImageName.parse("mongo:7.0"))
        .withNetwork(network)
        .withNetworkAliases("mongodb")
        .withCommand("mongod", "--storageEngine", "wiredTiger", "--noscripting")
        .withExposedPorts(27017)
        .waitingFor(Wait.forListeningPort());

    this.policies = dittoService("policies");
    this.things = dittoService("things");
    this.gateway = dittoService("gateway")
        .withEnv("ENABLE_PRE_AUTHENTICATION", "true");

    this.nginx = new GenericContainer<>(DockerImageName.parse("nginx:1.27-alpine"))
        .withNetwork(network)
        .withCopyToContainer(Transferable.of(NGINX_CONF), "/etc/nginx/nginx.conf")
        .withCopyToContainer(
            Transferable.of(USERNAME + ":{PLAIN}" + PASSWORD + "\n"),
            "/etc/nginx/nginx.htpasswd"
        )
        .withExposedPorts(NGINX_PORT)
        .waitingFor(Wait.forListeningPort());
  }

  public void start() {
    mongodb.start();
    Startables.deepStart(policies, things, gateway).join();
    nginx.start();
    awaitApiReady();
  }

  /**
   * Base URL of the WebSocket endpoint behind basic authentication, without the /ws/2 path,
   * which the Ditto client appends itself.
   */
  public String getWebSocketEndpoint() {
    return String.format("ws://%s:%d", nginx.getHost(), nginx.getMappedPort(NGINX_PORT));
  }

  public HttpResponse<String> get(String apiPath) throws IOException, InterruptedException {
    return send(request(apiPath).GET().build());
  }

  public HttpResponse<String> put(String apiPath, String json) throws IOException, InterruptedException {
    return send(request(apiPath)
        .header("Content-Type", "application/json")
        .PUT(HttpRequest.BodyPublishers.ofString(json))
        .build());
  }

  @Override
  public void close() {
    List<GenericContainer<?>> containers = List.of(nginx, gateway, things, policies, mongodb);
    List<RuntimeException> failures = new ArrayList<>();
    for (GenericContainer<?> container : containers) {
      try {
        container.stop();
      } catch (RuntimeException e) {
        failures.add(e);
      }
    }
    network.close();
    if (!failures.isEmpty()) {
      RuntimeException failure = failures.get(0);
      failures.stream().skip(1).forEach(failure::addSuppressed);
      throw failure;
    }
  }

  private GenericContainer<?> dittoService(String service) {
    return new GenericContainer<>(DockerImageName.parse("eclipse/ditto-" + service + ":" + DITTO_VERSION))
        .withNetwork(network)
        .withNetworkAliases(service, CLUSTER_ALIAS)
        .withEnv("BIND_HOSTNAME", "0.0.0.0")
        .withEnv("MONGO_DB_HOSTNAME", "mongodb")
        .withEnv("JAVA_TOOL_OPTIONS", JAVA_TOOL_OPTIONS)
        .withExposedPorts(STATUS_PORT)
        .waitingFor(Wait.forHttp("/alive").forPort(STATUS_PORT).withStartupTimeout(STARTUP_TIMEOUT));
  }

  /**
   * The services report alive before the cluster has formed and the shard regions are reachable.
   * A thing lookup only answers 404 once the gateway can route to the things and policies services.
   */
  private void awaitApiReady() {
    long deadline = System.nanoTime() + API_READY_TIMEOUT.toNanos();
    String lastStatus = "no response";
    while (System.nanoTime() < deadline) {
      try {
        HttpResponse<String> response = get("/api/2/things/org.apache.streampipes:readiness-probe");
        if (response.statusCode() == 404 || response.statusCode() == 200) {
          return;
        }
        lastStatus = response.statusCode() + " " + response.body();
      } catch (IOException e) {
        lastStatus = e.getMessage();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new IllegalStateException("Interrupted while waiting for Ditto", e);
      }
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new IllegalStateException("Interrupted while waiting for Ditto", e);
      }
    }
    throw new IllegalStateException("Ditto API did not become ready in time, last response: " + lastStatus);
  }

  private HttpRequest.Builder request(String apiPath) {
    String credentials = Base64.getEncoder()
        .encodeToString((USERNAME + ":" + PASSWORD).getBytes(StandardCharsets.UTF_8));
    return HttpRequest.newBuilder()
        .uri(URI.create(String.format("http://%s:%d%s", nginx.getHost(), nginx.getMappedPort(NGINX_PORT), apiPath)))
        .timeout(Duration.ofSeconds(30))
        .header("Authorization", "Basic " + credentials);
  }

  private HttpResponse<String> send(HttpRequest request) throws IOException, InterruptedException {
    return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
  }
}
