package org.streampipes.sinks.brokers.jvm.bufferrest;

import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;

import java.util.List;

public class BufferRestParameters extends EventSinkBindingParams {

  private String restEndpointURI;
  private List<String> fieldsToSend;
  private int bufferSize;

  public BufferRestParameters(DataSinkInvocation graph, List<String> fieldsToSend, String restEndpointURI, int bufferSize) {
    super(graph);
    this.fieldsToSend = fieldsToSend;
    this.restEndpointURI = restEndpointURI;
    this.bufferSize = bufferSize;
  }

  public List<String> getFieldsToSend() {
    return fieldsToSend;
  }

  public String getRestEndpointURI() {
    return restEndpointURI;
  }

  public int getBufferSize() {
    return bufferSize;
  }
}
