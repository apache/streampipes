package org.streampipes.sdk.builder;

import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.output.OutputStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by riemer on 20.11.2016.
 */
public class ProcessingElementBuilder extends AbstractProcessingElementBuilder<ProcessingElementBuilder, DataProcessorDescription> {


    private List<OutputStrategy> outputStrategies;

    private ProcessingElementBuilder(String id, String name, String description) {
        super(id, name, description, new DataProcessorDescription());
        this.outputStrategies = new ArrayList<>();
    }

    public static ProcessingElementBuilder create(String id, String label, String description)
    {
        return new ProcessingElementBuilder(id, label, description);
    }


    public ProcessingElementBuilder outputStrategy(OutputStrategy outputStrategy) {
        this.outputStrategies.add(outputStrategy);
        return me();
    }

    public ProcessingElementBuilder category(DataProcessorType... epaCategory) {
        this.elementDescription.setCategory(Arrays
                .stream(epaCategory)
                .map(Enum::name)
                .collect(Collectors.toList()));
        return me();
    }

    @Override
    public void prepareBuild() {
        super.prepareBuild();
        this.elementDescription.setOutputStrategies(outputStrategies);
    }

    @Override
    protected ProcessingElementBuilder me() {
        return this;
    }
}
