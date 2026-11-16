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

package org.apache.streampipes.service.core.storage;

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.couchdb.client.CouchServer;
import org.apache.streampipes.dataexplorer.ImageStore;
import org.apache.streampipes.model.dataset.DatasetMetadata;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.storage.couchdb.CouchDbCodecs;
import org.apache.streampipes.storage.couchdb.CouchDbStores;
import org.apache.streampipes.storage.couchdb.impl.system.ImageStorageImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Images written by the data lake sink's {@link ImageStore} are served by the core's {@link ImageStorageImpl}. Enable
 * with {@code -Dstreampipes.couchdb.integration=true}.
 */
@EnabledIfSystemProperty(named = "streampipes.couchdb.integration", matches = "true")
public class ImageStoreRoundTripIntegrationTest {

  @Test
  public void storedImageIsReadByTheImageStorage() throws IOException {
    try (var couchDb = new GenericContainer<>("couchdb:3.3.1")
        .withEnv("COUCHDB_USER", "admin")
        .withEnv("COUCHDB_PASSWORD", "admin")
        .withExposedPorts(5984)
        .waitingFor(Wait.forHttp("/_up").forStatusCode(200).withStartupTimeout(Duration.ofSeconds(60)))) {
      couchDb.start();
      var environment = mock(Environment.class, RETURNS_DEEP_STUBS);
      when(environment.getCouchDbProtocol().getValueOrDefault()).thenReturn("http");
      when(environment.getCouchDbHost().getValueOrDefault()).thenReturn(couchDb.getHost());
      when(environment.getCouchDbPort().getValueOrDefault()).thenReturn(couchDb.getMappedPort(5984));
      when(environment.getCouchDbUsername().getValueOrDefault()).thenReturn("admin");
      when(environment.getCouchDbPassword().getValueOrDefault()).thenReturn("admin");
      var measure = new DatasetMetadata();
      measure.setEventSchema(new EventSchema(List.of()));
      byte[] image = {(byte) 0xFF, (byte) 0xD8, 0, 1, 2, (byte) 0xFF, (byte) 0xD9};

      var imageStore = new ImageStore(measure, environment);
      try {
        imageStore.storeImage(image, "image-id");
      } finally {
        imageStore.close();
      }

      try (var server = CouchServer.builder()
          .url("http", couchDb.getHost(), couchDb.getMappedPort(5984))
          .credentials("admin", "admin")
          .build();
           var stored = new ImageStorageImpl(new CouchDbStores(server, CouchDbCodecs.defaults()))
               .getImageBytes("image-id")) {
        assertArrayEquals(image, stored.readAllBytes());
      }
    }
  }
}
