package org.streampipes.sdk.helpers;

import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.output.AppendOutputStrategy;
import org.streampipes.model.output.CustomOutputStrategy;
import org.streampipes.model.output.FixedOutputStrategy;
import org.streampipes.model.output.ListOutputStrategy;
import org.streampipes.model.output.KeepOutputStrategy;

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

    public static KeepOutputStrategy keep() {
        return new KeepOutputStrategy();
    }

    public static KeepOutputStrategy keep(boolean mergeInputStreams) {
        return new KeepOutputStrategy("Rename", mergeInputStreams);
    }

    public static ListOutputStrategy list(String propertyRuntimeName) {
        return new ListOutputStrategy(propertyRuntimeName);
    }
}
