package de.fzi.cep.sepa.manager.kpi.pipelineelements;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyNary;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.staticproperty.OneOfStaticProperty;

import java.net.URI;
import java.util.Arrays;

/**
 * Created by riemer on 03.10.2016.
 */
public class AggregationGenerator extends PipelineElementGenerator<SepaInvocation, AggregationSettings> {

    private static final String GROUP_BY = "groupBy";
    private static final String AGGREGATE = "aggregate";
    private static final String OUTPUT_EVERY = "outputEvery";
    private static final String TIME_WINDOW_SIZE = "timeWindow";
    private static final String AGGREGATE_OPERATION = "operation";

    private static final String DEFAULT_OUTPUT_FREQUENCY = "5";

    public AggregationGenerator(SepaInvocation pipelineElement, AggregationSettings aggregationSettings) {
        super(pipelineElement, aggregationSettings);
    }

    @Override
    public SepaInvocation makeInvocationGraph() {
        pipelineElement
                .getStaticProperties()
                .stream()
                .forEach(sp -> {
                    if (sp instanceof MappingPropertyNary) {
                        if (hasInternalName(sp, GROUP_BY)) {
                            if (settings.isPartition())
                                mappingPropertyNary(sp)
                                        .setMapsTo(Arrays.asList(URI.create(settings.getPartitionMapping())));
                        }
                    } else if (sp instanceof MappingPropertyUnary) {
                        if (hasInternalName(sp, AGGREGATE)) {
                            mappingPropertyUnary(sp)
                                    .setMapsTo(URI.create(settings.getEventPropertyMapping()));
                        }
                    } else if (sp instanceof FreeTextStaticProperty) {
                        if (hasInternalName(sp, OUTPUT_EVERY)) {
                            freeTextStaticProperty(sp)
                                    .setValue(DEFAULT_OUTPUT_FREQUENCY);
                        } else if (hasInternalName(sp, TIME_WINDOW_SIZE)) {
                            freeTextStaticProperty(sp)
                                    .setValue(String.valueOf(settings
                                            .getTimeWindow()
                                            .getValue()));
                        }
                    } else if (sp instanceof OneOfStaticProperty) {
                        if (hasInternalName(sp, AGGREGATE_OPERATION)) {
                            oneOfStaticProperty(sp)
                                    .getOptions()
                                    .stream()
                                    .filter(o -> o.getName().equals(getMapping(settings.getAggregationType())))
                                    .forEach(o -> o.setSelected(true));
                        }
                    }
                });

        return pipelineElement;
    }

    private String getMapping(String aggregationType) {
        if (aggregationType.equals("avg")) return "Average";
        else if (aggregationType.equals("sum")) return "Sum";
        else if (aggregationType.equals("min")) return "Min";
        else return "Max";
    }

}
