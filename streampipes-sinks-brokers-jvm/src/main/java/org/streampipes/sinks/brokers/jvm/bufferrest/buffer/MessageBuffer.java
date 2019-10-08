package org.streampipes.sinks.brokers.jvm.bufferrest.buffer;

import java.util.ArrayList;
import java.util.List;

public class MessageBuffer {
    private int bufferSize;
    private List<BufferListener> listeners;
    private List<String> messages;

    public MessageBuffer(int bufferSize) {
        this.bufferSize = bufferSize;
        this.messages = new ArrayList<String>();
        this.listeners = new ArrayList<BufferListener>();
    }

    public void addMessage(String message) {
        messages.add(message);

        if (bufferSize <= messages.size()) {
            notifyListeners();
            clearBuffer();
        }
    }

    private void clearBuffer() {
        this.messages = new ArrayList<String>();
    }

    public void addListener(BufferListener listener){
        listeners.add(listener);
    }

    public void removeListener(BufferListener listener){
        listeners.remove(listener);
    }

    private String getMessagesAsJsonString() {
        String messagesAsJson;
        if (bufferSize > 1) {
            messagesAsJson = "[";
            int i = 1;
            for (String message : messages) {
                messagesAsJson += message;
                if (i < messages.size()) {
                    messagesAsJson += ",";
                }
                i++;
            }
            messagesAsJson += "]";
        } else {
            messagesAsJson = messages.get(0);
        }
        return messagesAsJson;
    }
    private void notifyListeners(){
        String messagesJsonArray = getMessagesAsJsonString();
        for(BufferListener listener : listeners) {
            listener.bufferFull(messagesJsonArray);
        }
    }
}
