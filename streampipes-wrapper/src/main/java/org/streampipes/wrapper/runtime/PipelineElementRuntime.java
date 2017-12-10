package org.streampipes.wrapper.runtime;

import org.apache.commons.lang.RandomStringUtils;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.wrapper.params.runtime.RuntimeParams;

public abstract class PipelineElementRuntime<RP extends RuntimeParams> {

  protected RP params;
  protected String instanceId;

  public PipelineElementRuntime(RP params) {
    this.params = params;
    this.instanceId = RandomStringUtils.randomAlphabetic(8);
  }

  public abstract void initRuntime() throws SpRuntimeException;

  public abstract void postDiscard() throws SpRuntimeException;

  public abstract void bindRuntime() throws SpRuntimeException;

  public abstract void discardRuntime() throws SpRuntimeException;
}
