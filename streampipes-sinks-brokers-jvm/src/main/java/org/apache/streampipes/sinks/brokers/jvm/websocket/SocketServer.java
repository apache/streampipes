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
        broadcast("New connection: " + handshake.getResourceDescriptor()); //This method sends a message to all clients connected
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
