package org.streampipes.pe.sources.mhwirth.friction;

import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.pe.sources.mhwirth.config.AkerVariables;

/**
 * Created by riemer on 12.10.2016.
 */
public class SwivelFrictionCoefficientStream extends FrictionCoefficientStream {

    @Override
    public SpDataStream declareModel(DataSourceDescription sep) {
        return getPreparedEventStream(sep,
                AkerVariables.Friction_Swivel,
                AkerVariables.Friction_Swivel.originalTopic(),
                "friction_swivel");
    }
}
