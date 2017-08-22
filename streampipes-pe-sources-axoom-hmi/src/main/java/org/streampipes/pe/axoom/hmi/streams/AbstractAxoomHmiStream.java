package org.streampipes.pe.axoom.hmi.streams;

import org.streampipes.pe.axoom.hmi.config.AxoomHmiConfig;
import org.streampipes.sources.AbstractAlreadyExistingStream;

/**
 * Created by riemer on 19.03.2017.
 */
public abstract class AbstractAxoomHmiStream extends AbstractAlreadyExistingStream {

  protected AxoomHmiConfig eventType;

  public AbstractAxoomHmiStream(AxoomHmiConfig eventType) {
    super();
    this.eventType = eventType;
  }
}
