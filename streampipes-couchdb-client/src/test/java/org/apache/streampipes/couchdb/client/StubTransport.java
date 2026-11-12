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

package org.apache.streampipes.couchdb.client;

import org.apache.streampipes.couchdb.client.transport.CouchRequest;
import org.apache.streampipes.couchdb.client.transport.CouchResponse;
import org.apache.streampipes.couchdb.client.transport.CouchTransport;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Transport that records requests and replays scripted responses in order.
 */
class StubTransport implements CouchTransport {

  final List<CouchRequest> requests = new ArrayList<>();
  final List<Map<String, String>> headers = new ArrayList<>();
  private final Deque<CouchResponse> responses = new ArrayDeque<>();

  static StubTransport create() {
    return new StubTransport();
  }

  CouchServer server() {
    return CouchServer.builder().credentials("admin", "secret").transport(config -> this).build();
  }

  StubTransport respond(int status, String body) {
    return respond(status, body, Map.of());
  }

  StubTransport respond(int status, String body, Map<String, String> responseHeaders) {
    var lowerCase = new HashMap<String, String>();
    responseHeaders.forEach((k, v) -> lowerCase.put(k.toLowerCase(), v));
    responses.add(new CouchResponse(status, lowerCase,
        new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8))));
    return this;
  }

  CouchRequest request(int index) {
    return requests.get(index);
  }

  String body(int index) {
    byte[] body = requests.get(index).body();
    return body == null ? null : new String(body, StandardCharsets.UTF_8);
  }

  @Override
  public CouchResponse send(CouchRequest request, Map<String, String> requestHeaders) {
    requests.add(request);
    headers.add(requestHeaders);
    if (responses.isEmpty()) {
      throw new IllegalStateException("No response scripted for " + request.method() + " " + request.path());
    }
    return responses.poll();
  }

  @Override
  public void close() {
  }
}
