package de.fzi.cep.sepa.sources;

import de.fzi.cep.sepa.client.declarer.EventStreamDeclarer;

/**
 * Created by riemer on 12.03.2017.
 */
public abstract class AbstractAdapterIncludedStream implements EventStreamDeclarer {


  @Override
  public boolean isExecutable() {
    return true;
  }
}
