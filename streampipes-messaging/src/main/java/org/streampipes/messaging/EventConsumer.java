package org.streampipes.messaging;

import org.streampipes.model.impl.EventGrounding;

/**
 * Created by riemer on 01.10.2016.
 */
public interface EventConsumer<IN> {

    void openConsumer(EventGrounding eventGrounding) throws Exception;

    void onEvent(IN event);

    byte[] convertToByteArray(IN event);

    void closeConsumer();
}
