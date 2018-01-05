package org.streampipes.pe.sources.samples.friction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FrictionEventTransformer {

    private FrictionRawEvent rawEvent;
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    public FrictionEventTransformer(FrictionRawEvent rawEvent) {
        this.rawEvent = rawEvent;
    }

    public List<FrictionOutputEvent> transform() {
        List<FrictionOutputEvent> outputEvents = new ArrayList<>();
        outputEvents.add(makeEvent(rawEvent.getGearbox(), "gearbox"));
        outputEvents.add(makeEvent(rawEvent.getSwivel(), "swivel"));

        return outputEvents;
    }

    private FrictionOutputEvent makeEvent(FrictionValue frictionValue, String machineType) {
        FrictionOutputEvent outputEvent = new FrictionOutputEvent();
        outputEvent.setTimestamp(toTimestamp(rawEvent.getEnd()));
        outputEvent.setStd(0.0);
        outputEvent.setValue(frictionValue.getValue());
        outputEvent.setzScore(0.0);
        outputEvent.setEventId(machineType);

        return outputEvent;
    }

    private long toTimestamp(String end) {
        try {
            return sdf.parse(end.replaceAll("Z$", "+0000")).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }


}
