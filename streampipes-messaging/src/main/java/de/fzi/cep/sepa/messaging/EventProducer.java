package de.fzi.cep.sepa.messaging;

import java.io.Serializable;

/**
 * Created by riemer on 01.10.2016.
 */
public interface EventProducer extends Serializable {

    void openProducer();

    void publish(byte[] event);

    void publish(String event);

    void closeProducer();
}
