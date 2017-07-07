package org.streampipes.pe.sources.samples.mhwirth.friction;

import org.streampipes.pe.sources.samples.friction.FrictionEventTransformer;
import org.streampipes.pe.sources.samples.friction.FrictionOutputEvent;
import org.streampipes.pe.sources.samples.friction.FrictionRawEvent;
import org.streampipes.pe.sources.samples.friction.FrictionValue;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by riemer on 26.10.2016.
 */
public class TestFrictionEventTransformer {

    private final static double gearboxFrictionValue = 0.013653257862547412;
    private final static double swivelFrictionValue = 0.010293849764373883;

    @org.junit.Test
    public void testFrictionEventTransformation() {

        FrictionEventTransformer transformer = new FrictionEventTransformer(makeRawEvent());
        List<FrictionOutputEvent> outputEvents = transformer.transform();

        assertEquals(2, outputEvents.size());

        FrictionOutputEvent gearboxEvent = outputEvents.get(0);
        FrictionOutputEvent swivelEvent = outputEvents.get(1);

        assertEquals(gearboxFrictionValue, gearboxEvent.getValue(), 0.0002);
        assertEquals(swivelFrictionValue, swivelEvent.getValue(), 0.0002);

        assertEquals(1430449400776L, gearboxEvent.getTimestamp());

    }

    private FrictionRawEvent makeRawEvent() {
        FrictionRawEvent event = new FrictionRawEvent();
        event.setStart("2015-05-01T02:36:57.186Z");
        event.setEnd("2015-05-01T03:03:20.776Z");
        event.setGearbox(new FrictionValue(gearboxFrictionValue, 16.28865));
        event.setSwivel(new FrictionValue(swivelFrictionValue, 17.1134));

        return event;
    }
}
