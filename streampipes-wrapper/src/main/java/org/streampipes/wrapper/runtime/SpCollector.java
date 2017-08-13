package org.streampipes.wrapper.runtime;

public interface SpCollector<T> {

  void registerConsumer(String routeId, T consumer);

  void unregisterConsumer(String routeId);
}
