package org.streampipes.messaging;

public interface InternalEventProcessor<T> {

  void onEvent(T event);
}
