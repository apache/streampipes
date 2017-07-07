package org.streampipes.pe.sources.mhwirth.friction;

import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.pe.sources.mhwirth.config.AkerVariables;

/**
 * Created by riemer on 12.10.2016.
 */
public class SwivelFrictionCoefficientStream extends FrictionCoefficientStream {

    @Override
    public EventStream declareModel(SepDescription sep) {
        return getPreparedEventStream(sep,
                AkerVariables.Friction_Swivel,
                AkerVariables.Friction_Swivel.originalTopic(),
                "friction_swivel");
    }
}
