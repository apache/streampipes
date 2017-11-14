package org.streampipes.pe.sources.kd2.utils;

import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.pe.sources.kd2.vocabulary.Kd2;
import org.streampipes.sdk.helpers.Labels;

/**
 * Created by riemer on 20.11.2016.
 */
public class BiodataUtils {

    public static EventProperty getHeartRateProperty() {
        return EpProperties.doubleEp(Labels.empty(), "hrValue", Kd2.heartRate);
    }

    public static EventProperty getEdaProperty() {
        return EpProperties.doubleEp(Labels.empty(), "edaValue", Kd2.edaValue);
    }

    public static EventProperty getPulseProperty() {
        return EpProperties.doubleEp(Labels.empty(), "pulseValue", Kd2.pulseValue);
    }

    public static EventProperty getArousalProperty() {
        return EpProperties.doubleEp(Labels.empty(), "arousalValue", Kd2.arousalValue);
    }

}
