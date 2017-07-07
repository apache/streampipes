package de.fzi.cep.sepa.messaging;

/**
 * Created by riemer on 01.10.2016.
 */
public interface EventListener<T> {

    void onEvent(T event);
}
