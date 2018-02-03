package org.streampipes.sources;

import org.streampipes.container.declarer.EventStreamDeclarer;

public abstract class AbstractAdapterIncludedStream implements EventStreamDeclarer {


  @Override
  public boolean isExecutable() {
    return true;
  }
}
