package de.fzi.cep.sepa.manager.kpi.pipelineelements;

import de.fzi.cep.sepa.kpi.BinaryOperation;
import de.fzi.cep.sepa.kpi.UnaryOperation;
import de.fzi.cep.sepa.manager.kpi.mapping.IdMapper;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;

/**
 * Created by riemer on 06.10.2016.
 */
public class BinaryMathSettings extends Settings {

    private String mathOperation;
    private String leftPropertyId;
    private String rightPropertyId;

    public BinaryMathSettings() {

    }

    public BinaryMathSettings(String mathOperation, String leftPropertyId, String rightPropertyId) {
        this.mathOperation = mathOperation;
        this.leftPropertyId = leftPropertyId;
        this.rightPropertyId = rightPropertyId;
    }

    public String getMathOperation() {
        return mathOperation;
    }

    public void setMathOperation(String mathOperation) {
        this.mathOperation = mathOperation;
    }

    public String getLeftPropertyId() {
        return leftPropertyId;
    }

    public void setLeftPropertyId(String leftPropertyId) {
        this.leftPropertyId = leftPropertyId;
    }

    public String getRightPropertyId() {
        return rightPropertyId;
    }

    public void setRightPropertyId(String rightPropertyId) {
        this.rightPropertyId = rightPropertyId;
    }

    public static BinaryMathSettings makeSettings(NamedSEPAElement leftElement, NamedSEPAElement rightElement, BinaryOperation binaryPipelineOperation, IdMapper idMapper) {
        BinaryMathSettings settings = new BinaryMathSettings();
        settings.setMathOperation(binaryPipelineOperation
                .getArithmeticOperationType()
                .toString()
                .toLowerCase());


        EventProperty property1 = getProperty(leftElement, (UnaryOperation) binaryPipelineOperation.getLeft(), idMapper);
        EventProperty property2 = getProperty(rightElement, (UnaryOperation) binaryPipelineOperation.getRight(), idMapper);

        System.out.println(property1.getElementId());
        settings.setLeftPropertyId(property1.getElementId());
        System.out.println(property2.getElementId());
        settings.setRightPropertyId(property2.getElementId());

        return settings;

    }

    private static EventProperty getProperty(NamedSEPAElement element, UnaryOperation operation, IdMapper idMapper) {
        if (element instanceof EventStream) {
            return getMappedStreamProperty((EventStream) element, operation, idMapper);
        } else {
            return getAverageProperty((SepaInvocation) element);
        }
    }

    private static EventProperty getAverageProperty(SepaInvocation element) {
        return element.getOutputStream()
                .getEventSchema()
                .getEventProperties()
                .stream()
                .filter(e -> e.getRuntimeName().equals("aggregatedValue"))
                .findFirst()
                .get();
    }

    private static EventProperty getMappedStreamProperty(EventStream element, UnaryOperation operation, IdMapper idMapper) {
        System.out.println(operation.getEventPropertyName());
        return idMapper
                .getEventProperty(element, operation.getEventPropertyName());
    }
}
