package org.streampipes.connect;

import org.streampipes.connect.firstconnector.format.Format;

import java.util.Map;

public class SendToKafka implements EmitBinaryEvent {

    private Format format;

    public SendToKafka(Format format) {
        this.format = format;
    }

    @Override
    public Boolean emit(byte[] event) {

        Map<String, Object> result = format.parse(event);
        System.out.println("send to kafka: " + result);

        return true;
    }
}
