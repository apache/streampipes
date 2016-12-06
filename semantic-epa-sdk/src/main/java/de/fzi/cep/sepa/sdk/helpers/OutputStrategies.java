package de.fzi.cep.sepa.sdk.helpers;

import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.CustomOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.FixedOutputStrategy;

import java.util.List;

/**
 * Created by riemer on 06.12.2016.
 */
public class OutputStrategies {

    public static FixedOutputStrategy fixed(List<EventProperty> outputProperties) {
        return new FixedOutputStrategy(outputProperties);
    }

    public static CustomOutputStrategy custom() {
        return new CustomOutputStrategy();
    }

    public static CustomOutputStrategy custom(boolean outputBoth) {
        return new CustomOutputStrategy(outputBoth);
    }

    public static AppendOutputStrategy append(List<EventProperty> appendProperties) {
        return new AppendOutputStrategy(appendProperties);
    }
}
