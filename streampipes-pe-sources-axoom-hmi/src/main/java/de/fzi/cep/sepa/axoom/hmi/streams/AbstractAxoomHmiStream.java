package de.fzi.cep.sepa.axoom.hmi.streams;

import de.fzi.cep.sepa.axoom.hmi.config.AxoomHmiConfig;
import de.fzi.cep.sepa.sources.AbstractAlreadyExistingStream;

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
