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

package org.apache.streampipes.sinks.brokers.jvm.websocket;

import org.apache.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.apache.streampipes.model.runtime.Event;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.Map;

public class SocketServer extends WebSocketServer {

  private JsonDataFormatDefinition dataFormatDefinition;

  public SocketServer(int port) {
    super(new InetSocketAddress(port));
    dataFormatDefinition = new JsonDataFormatDefinition();
  }

  @Override
  public void onOpen(WebSocket conn, ClientHandshake handshake) {
    conn.send("Welcome!"); //This method sends a message to the new client
    broadcast(
        "New connection: " + handshake.getResourceDescriptor()); //This method sends a message to all clients connected
    System.out.println(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " connected.");
  }

  @Override
  public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    // do nothing special
  }

  @Override
  public void onMessage(WebSocket conn, String message) {
    System.out.println(conn + ": " + message);
  }

  @Override
  public void onError(WebSocket conn, Exception ex) {
    ex.printStackTrace();
  }

  @Override
  public void onStart() {
    System.out.println("Server started!");
    setConnectionLostTimeout(0);
    setConnectionLostTimeout(100);
  }

  public void onEvent(Event event) {
    Map<String, Object> rawEvent = event.getRaw();
    broadcast(dataFormatDefinition.fromMap(rawEvent));
  }
}
