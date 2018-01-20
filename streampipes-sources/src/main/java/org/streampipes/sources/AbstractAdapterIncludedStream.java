package org.streampipes.sources;

import org.streampipes.container.declarer.DataStreamDeclarer;

public abstract class AbstractAdapterIncludedStream implements DataStreamDeclarer {


  @Override
  public boolean isExecutable() {
    return true;
  }
}
