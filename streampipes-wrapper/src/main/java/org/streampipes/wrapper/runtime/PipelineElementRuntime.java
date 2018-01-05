package org.streampipes.wrapper.runtime;

import org.apache.commons.lang.RandomStringUtils;
import org.streampipes.commons.exceptions.SpRuntimeException;

public abstract class PipelineElementRuntime {

  protected String instanceId;

  public PipelineElementRuntime() {
    this.instanceId = RandomStringUtils.randomAlphabetic(8);
  }

  public abstract void prepareRuntime() throws SpRuntimeException;

  public abstract void postDiscard() throws SpRuntimeException;

  public abstract void bindRuntime() throws SpRuntimeException;

  public abstract void discardRuntime() throws SpRuntimeException;
}
