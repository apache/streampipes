package org.streampipes.pe.axoom.hmi.streams;

import org.streampipes.pe.axoom.hmi.config.AxoomHmiConfig;
import org.streampipes.sources.AbstractAlreadyExistingStream;

public abstract class AbstractAxoomHmiStream extends AbstractAlreadyExistingStream {

  protected AxoomHmiConfig eventType;

  public AbstractAxoomHmiStream(AxoomHmiConfig eventType) {
    super();
    this.eventType = eventType;
  }
}
