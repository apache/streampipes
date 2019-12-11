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

package org.apache.streampipes.connect.protocol.stream;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.apache.streampipes.connect.adapter.model.pipeline.AdapterPipeline;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/** This example demonstrates how to create a websocket connection to a server. Only the most important callbacks are overloaded. */
public class WebsocketClient extends WebSocketClient {

  private AdapterPipeline pipeline;
  private JsonParser parser;

  public WebsocketClient(URI serverUri , Draft draft ) {
    super( serverUri, draft );
    this.parser = new JsonParser();
  }

  public WebsocketClient(AdapterPipeline pipeline, URI serverURI ) {
    super( serverURI );
    this.pipeline = pipeline;
    this.parser = new JsonParser();
  }

  public WebsocketClient( URI serverUri, Map<String, String> httpHeaders ) {
    super(serverUri, httpHeaders);
  }

  @Override
  public void onOpen( ServerHandshake handshakedata ) {
    //send("Hello, it is me. Mario :)");
    send("{\"op\":\"subscribe\",\"id\":\"subscribe:/map:1\",\"type\":\"nav_msgs/OccupancyGrid\"," +
            "\"topic\":\"/map\",\"compression\":\"png\",\"throttle_rate\":0,\"queue_length\":0}");
    System.out.println( "opened connection" );
    // if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
  }

  @Override
  public void onMessage( String message ) {
    System.out.println( "received: " + message );
    JsonElement jsonElement = parser.parse(message);
    Map<String, Object> event = new HashMap<>();
    event.put("timestamp", System.currentTimeMillis());
    event.put("data", jsonElement.getAsJsonObject().get("data").getAsString());
    pipeline.process(event);
  }

  @Override
  public void onClose( int code, String reason, boolean remote ) {
    // The codecodes are documented in class org.java_websocket.framing.CloseFrame
    System.out.println( "Connection closed by " + ( remote ? "remote peer" : "us" ) + " Code: " + code + " Reason: " + reason );
  }

  @Override
  public void onError( Exception ex ) {
    ex.printStackTrace();
    // if the error is fatal then onClose will be called additionally
  }

//  public static void main( String[] args ) throws URISyntaxException {
//    Map<String, String> header = new HashMap<>();
//    WebsocketClient c = new WebsocketClient( new URI( "ws://192.168.178.40:9090" )); // more about
//    // drafts here:
//    // http://github.com/TooTallNate/Java-WebSocket/wiki/Drafts
//    c.connect();
//  }

}