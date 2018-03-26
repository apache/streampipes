package org.streampipes.connect.firstconnector.protocol;

import org.streampipes.model.modelconnect.ProtocolDescription;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.connect.firstconnector.format.Format;
import org.streampipes.connect.firstconnector.format.Parser;


public abstract class Protocol implements Runnable {

    public abstract Protocol getInstance(ProtocolDescription protocolDescription, Parser parser, Format format);

    public abstract ProtocolDescription declareModel();

    public abstract EventSchema getSchema();

    /*
        This method is used when the adapter is started to send constantly events to Kafka
     */
    public abstract void run(String broker, String topic);

    public abstract String getId();
}
