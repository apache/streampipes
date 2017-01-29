package de.fzi.cep.sepa.sdk.helpers;

import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.CustomOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.FixedOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.ListOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.RenameOutputStrategy;

import java.util.Arrays;
import java.util.List;

/**
 * Created by riemer on 06.12.2016.
 */
public class OutputStrategies {

    public static CustomOutputStrategy custom() {
        return new CustomOutputStrategy();
    }

    public static CustomOutputStrategy custom(boolean outputBoth) {
        return new CustomOutputStrategy(outputBoth);
    }

    public static AppendOutputStrategy append(EventProperty... appendProperties) {
        return new AppendOutputStrategy(Arrays.asList(appendProperties));
    }

    public static AppendOutputStrategy append(List<EventProperty> appendProperties) {
        return new AppendOutputStrategy(appendProperties);
    }

    public static FixedOutputStrategy fixed(EventProperty... appendProperties) {
        return new FixedOutputStrategy(Arrays.asList(appendProperties));
    }

    public static FixedOutputStrategy fixed(List<EventProperty> appendProperties) {
        return new FixedOutputStrategy(appendProperties);
    }

    public static RenameOutputStrategy keep() {
        return new RenameOutputStrategy();
    }

    public static ListOutputStrategy list(String propertyRuntimeName) {
        return new ListOutputStrategy(propertyRuntimeName);
    }
}
