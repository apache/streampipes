package org.streampipes.pe.mixed.flink.samples.file;

import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;

public class FileSinkParameters extends EventSinkBindingParams {

  public FileSinkParameters(DataSinkInvocation graph) {
    super(graph);
  }
}
