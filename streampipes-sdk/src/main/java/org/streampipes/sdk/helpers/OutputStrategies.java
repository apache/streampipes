package org.streampipes.sdk.helpers;

import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.output.AppendOutputStrategy;
import org.streampipes.model.output.CustomOutputStrategy;
import org.streampipes.model.output.FixedOutputStrategy;
import org.streampipes.model.output.ListOutputStrategy;
import org.streampipes.model.output.KeepOutputStrategy;

import java.util.Arrays;
import java.util.List;

public class OutputStrategies {

    /**
     * Creates a {@link org.streampipes.model.output.CustomOutputStrategy}. Custom output strategies let pipeline
     * developers decide which events are produced by the corresponding pipeline element.
     * @return CustomOutputStrategy
     */
    public static CustomOutputStrategy custom() {
        return new CustomOutputStrategy();
    }

    /**
     * Creates a {@link org.streampipes.model.output.CustomOutputStrategy}.
     * @param outputBoth If two input streams are expected by a pipeline element, you can use outputBoth to indicate
     *                   whether the properties of both input streams should be available to the pipeline developer for
     *                   selection.
     * @return CustomOutputStrategy
     */
    public static CustomOutputStrategy custom(boolean outputBoth) {
        return new CustomOutputStrategy(outputBoth);
    }


    /**
     * Creates a {@link org.streampipes.model.output.AppendOutputStrategy}. Append output strategies add additional
     * properties to an input event stream.
     * @param appendProperties An arbitrary number of event properties that are appended to any input stream.
     * @return AppendOutputStrategy
     */
    public static AppendOutputStrategy append(EventProperty... appendProperties) {
        return new AppendOutputStrategy(Arrays.asList(appendProperties));
    }

    public static AppendOutputStrategy append(List<EventProperty> appendProperties) {
        return new AppendOutputStrategy(appendProperties);
    }

    /**
     * Creates a {@link org.streampipes.model.output.FixedOutputStrategy}. Fixed output strategies always output the
     * schema defined by the pipeline element itself.
     * @param fixedProperties An arbitrary number of event properties that form the output event schema
     * @return FixedOutputStrategy
     */
    public static FixedOutputStrategy fixed(EventProperty... fixedProperties) {
        return new FixedOutputStrategy(Arrays.asList(fixedProperties));
    }

    public static FixedOutputStrategy fixed(List<EventProperty> appendProperties) {
        return new FixedOutputStrategy(appendProperties);
    }

    /**
     * Creates a {@link org.streampipes.model.output.KeepOutputStrategy}. Keep output strategies do not change the
     * schema of an input event, i.e., the output schema matches the input schema.
     * @return KeepOutputStrategy
     */
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
