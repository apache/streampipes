package org.streampipes.wrapper.runtime;

import org.streampipes.wrapper.params.runtime.RuntimeParams;

public abstract class PipelineElementRuntime<RP extends RuntimeParams> {

  protected RP params;

  public PipelineElementRuntime(RP params) {
    this.params = params;
  }
}
