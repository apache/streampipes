package org.apache.streampipes.connect.iiot.protocol.stream.websocket;

import org.apache.streampipes.messaging.InternalEventProcessor;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class SpWebSocketClient extends WebSocketClient {

    private InternalEventProcessor<byte[]> source;

    private static final Logger LOG = LoggerFactory.getLogger(SpWebSocketClient.class);
    private int messageCount;

    public SpWebSocketClient(URI serverURI, InternalEventProcessor<byte[]> source) {
        super(serverURI);
        this.source = source;
        this.messageCount = 0;
    }

    public SpWebSocketClient(URI serverURI, InternalEventProcessor<byte[]> source, HashMap<String, String> httpHeaders) {
        super(serverURI, httpHeaders);
        this.source = source;
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
        this.source.onEvent(s.getBytes(StandardCharsets.UTF_8));
        this.messageCount ++;
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
