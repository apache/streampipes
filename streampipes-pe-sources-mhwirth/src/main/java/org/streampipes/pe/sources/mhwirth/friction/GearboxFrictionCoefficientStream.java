package org.streampipes.pe.sources.mhwirth.friction;

import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.pe.sources.mhwirth.config.AkerVariables;

/**
 * Created by riemer on 11.10.2016.
 */
public class GearboxFrictionCoefficientStream extends FrictionCoefficientStream {

    @Override
    public SpDataStream declareModel(DataSourceDescription sep) {
        return getPreparedEventStream(sep,
                AkerVariables.Friction_Gearbox,
                AkerVariables.Friction_Gearbox.originalTopic(),
                "friction_gearbox");

    }

}
