package org.streampipes.messaging;

import java.io.Serializable;

/**
 * Created by riemer on 01.10.2016.
 */
public interface EventProducer<OUT> extends Serializable {

    void openProducer();

    void publish(OUT event);

    OUT convertFromByteArray(byte[] event);

    void closeProducer();
}
