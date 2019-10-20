
package org.streampipes.sinks.brokers.jvm.bufferrest;

import org.apache.commons.io.Charsets;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.dataformat.SpDataFormatDefinition;
import org.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.streampipes.model.runtime.Event;
import org.streampipes.sinks.brokers.jvm.bufferrest.buffer.BufferListener;
import org.streampipes.sinks.brokers.jvm.bufferrest.buffer.MessageBuffer;
import org.streampipes.wrapper.context.EventSinkRuntimeContext;
import org.streampipes.wrapper.runtime.EventSink;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public class BufferRest implements EventSink<BufferRestParameters>, BufferListener {

  private static Logger LOG;
  private List<String> fieldsToSend;
  private SpDataFormatDefinition dataFormatDefinition;
  private String restEndpointURI;
  private MessageBuffer buffer;

  public BufferRest() {
    this.dataFormatDefinition = new JsonDataFormatDefinition();
  }

  @Override
  public void onInvocation(BufferRestParameters parameters, EventSinkRuntimeContext runtimeContext) {
    this.fieldsToSend = parameters.getFieldsToSend();
    this.restEndpointURI = parameters.getRestEndpointURI();
    this.buffer = new MessageBuffer(parameters.getBufferSize());
    this.buffer.addListener(this);
  }

  @Override
  public void onEvent(Event event) {
    Map<String, Object> outEventMap = event.getSubset(fieldsToSend).getRaw();
    try {
      String json = new String(dataFormatDefinition.fromMap(outEventMap));
      this.buffer.addMessage(json);
    } catch (SpRuntimeException e) {
      LOG.error("Could not parse incoming event");
    }
  }

  @Override
  public void onDetach() {
    buffer.removeListener(this);
  }

  @Override
  public void bufferFull(String messagesJsonArray) {
    try {
      Request.Post(restEndpointURI).body(new StringEntity(messagesJsonArray, Charsets.UTF_8)).execute();
    } catch (IOException e) {
      LOG.error("Could not reach endpoint at {}", restEndpointURI);
    }
  }
}
