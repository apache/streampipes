package org.streampipes.pe.sources.mhwirth.friction;

import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.pe.sources.mhwirth.config.AkerVariables;

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
