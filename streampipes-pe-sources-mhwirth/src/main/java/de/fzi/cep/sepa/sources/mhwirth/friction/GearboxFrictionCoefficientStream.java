package de.fzi.cep.sepa.sources.mhwirth.friction;

import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.sources.mhwirth.config.AkerVariables;

/**
 * Created by riemer on 11.10.2016.
 */
public class GearboxFrictionCoefficientStream extends FrictionCoefficientStream {

    @Override
    public EventStream declareModel(SepDescription sep) {
        return getPreparedEventStream(sep,
                AkerVariables.Friction_Gearbox,
                AkerVariables.Friction_Gearbox.originalTopic(),
                "friction_gearbox");

    }

}
