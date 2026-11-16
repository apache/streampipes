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

package org.apache.streampipes.dataexplorer;

import org.apache.streampipes.couchdb.client.CouchServer;
import org.apache.streampipes.couchdb.client.transport.CouchRequest;
import org.apache.streampipes.couchdb.client.transport.CouchResponse;
import org.apache.streampipes.couchdb.client.transport.CouchTransport;
import org.apache.streampipes.model.dataset.DatasetMetadata;
import org.apache.streampipes.model.runtime.EventFactory;
import org.apache.streampipes.model.runtime.SchemaInfo;
import org.apache.streampipes.model.runtime.SourceInfo;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.vocabulary.SPSensor;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImageStoreTest {

  private final RecordingTransport transport = new RecordingTransport();
  private final CouchServer server = CouchServer.builder().transport(config -> transport).build();

  @Test
  void createsAMissingDatabaseWhenStarted() {
    transport.databaseExists = false;

    new ImageStore(measure(), server);

    assertEquals(List.of("HEAD /images", "PUT /images"), transport.requestLines());
  }

  @Test
  void doesNotCreateAnExistingDatabase() {
    new ImageStore(measure(), server);

    assertEquals(List.of("HEAD /images"), transport.requestLines());
  }

  @Test
  void storesImageAsAttachmentOfANewDocument() {
    var store = new ImageStore(measure(), server);

    store.storeImage(new byte[] {1, 2, 3}, "image-id");

    var request = transport.requests.get(transport.requests.size() - 1);
    assertEquals("PUT", request.method());
    assertEquals("/images/image-id/image-id", request.path());
    assertEquals("image/jpeg", request.contentType());
    assertArrayEquals(new byte[] {1, 2, 3}, request.body());
  }

  @Test
  void onEventStoresDecodedImageAndReplacesItWithTheDocumentId() {
    var store = new ImageStore(measure(), server);
    var fields = new HashMap<String, Object>();
    fields.put("image", Base64.getEncoder().encodeToString(new byte[] {4, 5}));
    fields.put("other", "kept");
    var event = EventFactory.fromMap(fields, new SourceInfo("topic", "s0"), new SchemaInfo(null, new ArrayList<>()));

    store.onEvent(event);

    var request = transport.requests.get(transport.requests.size() - 1);
    assertArrayEquals(new byte[] {4, 5}, request.body());
    String id = event.getFieldBySelector("s0::image").getAsPrimitive().getAsString();
    assertEquals("/images/" + id + "/" + id, request.path());
    assertEquals("kept", event.getFieldBySelector("s0::other").getAsPrimitive().getAsString());
  }

  @Test
  void closeClosesTheConnections() throws IOException {
    var store = new ImageStore(measure(), server);

    store.close();

    assertTrue(transport.closed);
  }

  private static DatasetMetadata measure() {
    var image = new EventPropertyPrimitive();
    image.setRuntimeName("image");
    image.setSemanticType(SPSensor.IMAGE);
    var other = new EventPropertyPrimitive();
    other.setRuntimeName("other");
    var measure = new DatasetMetadata();
    measure.setEventSchema(new EventSchema(List.of(image, other)));
    return measure;
  }

  private static class RecordingTransport implements CouchTransport {

    private final List<CouchRequest> requests = new ArrayList<>();
    private boolean databaseExists = true;
    private boolean closed;

    @Override
    public CouchResponse send(CouchRequest request, Map<String, String> headers) {
      requests.add(request);
      int status = "HEAD".equals(request.method()) ? (databaseExists ? 200 : 404) : 201;
      String body = "{\"ok\":true,\"id\":\"id\",\"rev\":\"1-a\"}";
      return new CouchResponse(status, Map.of(), new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public void close() {
      closed = true;
    }

    private List<String> requestLines() {
      return requests.stream().map(r -> r.method() + " " + r.path()).toList();
    }
  }
}
