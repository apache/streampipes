package de.fzi.cep.sepa.sources.kd2.utils;

import de.fzi.cep.sepa.sdk.helpers.EpProperties;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.sources.kd2.vocabulary.Kd2;

/**
 * Created by riemer on 20.11.2016.
 */
public class BiodataUtils {

    public static EventProperty getHeartRateProperty() {
        return EpProperties.doubleEp("hrValue", Kd2.heartRate);
    }

    public static EventProperty getEdaProperty() {
        return EpProperties.doubleEp("edaValue", Kd2.edaValue);
    }

    public static EventProperty getPulseProperty() {
        return EpProperties.doubleEp("pulseValue", Kd2.pulseValue);
    }

    public static EventProperty getArousalProperty() {
        return EpProperties.doubleEp("arousalValue", Kd2.arousalValue);
    }

}
