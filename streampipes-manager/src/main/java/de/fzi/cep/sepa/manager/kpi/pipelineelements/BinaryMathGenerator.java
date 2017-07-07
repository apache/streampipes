package de.fzi.cep.sepa.manager.kpi.pipelineelements;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.staticproperty.OneOfStaticProperty;

import java.net.URI;

/**
 * Created by riemer on 06.10.2016.
 */
public class BinaryMathGenerator extends PipelineElementGenerator<SepaInvocation, BinaryMathSettings> {

    private static final String LEFT_OPERAND = "leftOperand";
    private static final String RIGHT_OPERAND = "rightOperand";
    private static final String OPERATION = "operation";

    public BinaryMathGenerator(SepaInvocation pipelineElement, BinaryMathSettings settings) {
        super(pipelineElement, settings);
    }

    @Override
    public SepaInvocation makeInvocationGraph() {
        pipelineElement
                .getStaticProperties()
                .stream()
                .forEach(sp -> {
                    if (sp instanceof MappingPropertyUnary) {
                        if (hasInternalName(sp, LEFT_OPERAND)) {
                            mappingPropertyUnary(sp)
                                    .setMapsTo(URI.create(settings.getLeftPropertyId()));
                        } else if (hasInternalName(sp, RIGHT_OPERAND)) {
                            mappingPropertyUnary(sp)
                                    .setMapsTo(URI.create(settings.getRightPropertyId()));
                        }
                    } else if (sp instanceof OneOfStaticProperty) {
                        if (hasInternalName(sp, OPERATION)) {
                            oneOfStaticProperty(sp)
                                    .getOptions()
                                    .stream()
                                    .filter(o -> o.getName().equals(getMapping(settings.getMathOperation())))
                                    .forEach(o -> o.setSelected(true));
                        }
                    }
                });

        return pipelineElement;
    }

    private String getMapping(String mathOperation) {
        if (mathOperation.equals("sum")) return "+";
        else if (mathOperation.equals("diff")) return "-";
        else if (mathOperation.equals("multiply")) return "*";
        else return "/";
    }
}
