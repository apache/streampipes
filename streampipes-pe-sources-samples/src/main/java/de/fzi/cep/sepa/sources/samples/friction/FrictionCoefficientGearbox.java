package de.fzi.cep.sepa.sources.samples.friction;

import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;

/**
 * Created by riemer on 26.10.2016.
 */
public class FrictionCoefficientGearbox extends FrictionCoefficient implements de.fzi.cep.sepa.client.declarer.EventStreamDeclarer {

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
