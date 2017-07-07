package org.streampipes.pe.sources.samples.friction;

import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.container.declarer.EventStreamDeclarer;

/**
 * Created by riemer on 26.10.2016.
 */
public class FrictionCoefficientGearbox extends FrictionCoefficient implements EventStreamDeclarer {

    protected FrictionCoefficientGearbox() {
        super(FrictionVariable.Gearbox);
    }

    @Override
    public EventStream declareModel(SepDescription sep) {
        return prepareStream(sep);
    }

    @Override
    public void executeStream() {

    }

    @Override
    public boolean isExecutable() {
        return false;
    }
}
