package org.streampipes.pe.sinks.standalone.samples.file;

import org.streampipes.model.impl.graph.SecInvocation;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;

public class FileParameters extends EventSinkBindingParams {

  private String path;

  public FileParameters(SecInvocation graph, String path) {
    super(graph);
    this.path = path;
  }

  public String getPath() {
    return path;
  }

}
