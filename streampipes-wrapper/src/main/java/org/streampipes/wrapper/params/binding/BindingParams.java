package org.streampipes.wrapper.params.binding;

import org.streampipes.model.InvocableSEPAElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BindingParams<I extends InvocableSEPAElement> {

  protected I graph;
  private List<InputStreamParams> inputStreamParams = new ArrayList<>();

  private final Map<String, Map<String, Object>> inEventTypes;

  BindingParams(I graph) {
    this.graph = graph;
    this.inEventTypes = new HashMap<>();
    graph.getInputStreams().forEach(is ->
            inEventTypes.put(is.getEventGrounding().getTransportProtocol().getTopicName(), is.getEventSchema().toRuntimeMap()));

    graph.getInputStreams().forEach(s -> inputStreamParams.add(new InputStreamParams(s)));
  }

  public I getGraph() {
    return graph;
  }

  public List<InputStreamParams> getInputStreamParams() {
    return inputStreamParams;
  }

  public Map<String, Map<String, Object>> getInEventTypes() {
    return inEventTypes;
  }

}
