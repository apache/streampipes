package de.fzi.cep.sepa.axoom.hmi.streams;

import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.sources.AbstractAlreadyExistingStream;

/**
 * Created by riemer on 16.03.2017.
 */
public class TrendStream extends AbstractAlreadyExistingStream {

  @Override
  public EventStream declareModel(SepDescription sep) {
    return null;
  }
}
