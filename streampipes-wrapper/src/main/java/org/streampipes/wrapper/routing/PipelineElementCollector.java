package org.streampipes.wrapper.routing;

import org.streampipes.commons.exceptions.SpRuntimeException;

public interface PipelineElementCollector<C> {

  void registerConsumer(String routeId, C consumer);

  void unregisterConsumer(String routeId);

  void connect() throws SpRuntimeException;

  void disconnect() throws SpRuntimeException;
}
