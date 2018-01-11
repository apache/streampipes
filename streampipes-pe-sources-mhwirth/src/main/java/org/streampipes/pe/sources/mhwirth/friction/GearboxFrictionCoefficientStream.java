package org.streampipes.pe.sources.mhwirth.friction;

import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.pe.sources.mhwirth.config.AkerVariables;

public class GearboxFrictionCoefficientStream extends FrictionCoefficientStream {

    @Override
    public SpDataStream declareModel(DataSourceDescription sep) {
        return getPreparedEventStream(sep,
                AkerVariables.Friction_Gearbox,
                AkerVariables.Friction_Gearbox.originalTopic(),
                "friction_gearbox");

    }

}
