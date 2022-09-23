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
package org.apache.streampipes.connect.iiot.protocol.stream.websocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashMap;

public class SpWebSocketClient extends WebSocketClient {

  private final WebsocketAdapter websocketAdapter;

  private static final Logger LOG = LoggerFactory.getLogger(SpWebSocketClient.class);
  private int messageCount;

  public SpWebSocketClient(URI serverURI, HashMap<String, String> httpHeaders, WebsocketAdapter websocketAdapter) {
    super(serverURI, httpHeaders);
    this.websocketAdapter = websocketAdapter;
    this.messageCount = 0;
  }

  @Override
  public void onOpen(ServerHandshake handshakeData) {
    //send("Hello, it's me. Apache Streampipes.");
    LOG.info("Connected successfully to " + this.uri);
    LOG.info("Submitted handshake data: " + handshakeData.toString());
  }

  @Override
  public void onMessage(String s) {
    this.websocketAdapter.onEvent(s);
    this.messageCount++;
  }

  @Override
  public void onClose(int i, String s, boolean b) {
    LOG.info("Websocket closed with string: " + s + " and boolean: " + b);
  }

  @Override
  public void onError(Exception e) {
    LOG.info(e.getMessage());
  }

  public int getMessageCount() {
    return messageCount;
  }
}
