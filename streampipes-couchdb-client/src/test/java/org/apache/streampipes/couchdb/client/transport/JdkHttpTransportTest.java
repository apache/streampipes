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

package org.apache.streampipes.couchdb.client.transport;

import org.apache.streampipes.couchdb.client.CouchServerConfig;
import org.apache.streampipes.couchdb.client.exception.CouchDbTransportException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JdkHttpTransportTest {

  private ServerSocket serverSocket;
  private JdkHttpTransport transport;
  private final AtomicInteger connectionsToDrop = new AtomicInteger();
  private final List<String> requestLines = new CopyOnWriteArrayList<>();

  @BeforeEach
  void startServer() throws IOException {
    serverSocket = new ServerSocket(0, 50, InetAddress.getLoopbackAddress());
    Thread.ofVirtual().start(this::serve);
    transport = new JdkHttpTransport(new CouchServerConfig(
        "http://127.0.0.1:" + serverSocket.getLocalPort(), null, null, Duration.ofSeconds(1), Duration.ofSeconds(2)));
  }

  @AfterEach
  void stopServer() throws IOException {
    transport.close();
    serverSocket.close();
  }

  @Test
  void putIsRepeatedOnceIfTheConnectionIsClosedWithoutAnswer() {
    connectionsToDrop.set(1);

    try (var response = transport.send(CouchRequest.put("/db/doc", body()), Map.of())) {
      assertEquals(201, response.status());
    }

    assertEquals(List.of("PUT /db/doc HTTP/1.1", "PUT /db/doc HTTP/1.1"), requestLines);
  }

  @Test
  void putFailsIfTheRepeatFailsToo() {
    connectionsToDrop.set(2);

    assertThrows(CouchDbTransportException.class, () -> transport.send(CouchRequest.put("/db/doc", body()), Map.of()));

    assertEquals(2, requestLines.size());
  }

  @Test
  void postIsNotRepeatedBecauseItCouldWriteTwice() {
    connectionsToDrop.set(1);

    assertThrows(CouchDbTransportException.class, () -> transport.send(CouchRequest.post("/db", body()), Map.of()));

    assertEquals(List.of("POST /db HTTP/1.1"), requestLines);
  }

  @Test
  void putIsNotRepeatedAfterATimeout() {
    connectionsToDrop.set(-1);

    assertThrows(CouchDbTransportException.class, () -> transport.send(CouchRequest.put("/db/doc", body()), Map.of()));

    assertEquals(1, requestLines.size());
  }

  private static byte[] body() {
    return "{}".getBytes(StandardCharsets.UTF_8);
  }

  private void serve() {
    while (!serverSocket.isClosed()) {
      try {
        var socket = serverSocket.accept();
        Thread.ofVirtual().start(() -> answer(socket));
      } catch (IOException e) {
        return;
      }
    }
  }

  private void answer(Socket socket) {
    try (socket) {
      var reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.ISO_8859_1));
      requestLines.add(reader.readLine());
      if (connectionsToDrop.get() < 0) {
        Thread.sleep(Duration.ofSeconds(5));
      }
      if (connectionsToDrop.getAndDecrement() > 0) {
        return;
      }
      String response = "HTTP/1.1 201 Created\r\nContent-Length: 2\r\nConnection: close\r\n\r\n{}";
      socket.getOutputStream().write(response.getBytes(StandardCharsets.ISO_8859_1));
    } catch (IOException e) {
      // The client went away.
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
