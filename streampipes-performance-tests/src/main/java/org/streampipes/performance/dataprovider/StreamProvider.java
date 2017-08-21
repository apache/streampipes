package org.streampipes.performance.dataprovider;

import org.streampipes.model.impl.EventStream;

public interface StreamProvider {

  EventStream getStreamDescription();
}
