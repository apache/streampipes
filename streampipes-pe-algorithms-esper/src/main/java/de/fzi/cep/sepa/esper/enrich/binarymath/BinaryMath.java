package de.fzi.cep.sepa.esper.enrich.binarymath;

import com.espertech.esper.client.soda.*;
import de.fzi.cep.sepa.esper.EsperEventEngine;
import de.fzi.cep.sepa.esper.enrich.math.Operation;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class BinaryMath extends EsperEventEngine<BinaryMathParameter> {

    private static final Logger logger = Logger.getAnonymousLogger();

    @Override
    protected List<String> statements(BinaryMathParameter bindingParameters) {
        List<String> statements = new ArrayList<String>();

        EPStatementObjectModel model = new EPStatementObjectModel();
        model.selectClause(makeSelectClause(bindingParameters));
        model.fromClause(new FromClause()
                .add(FilterStream
                        .create(fixEventName(bindingParameters
                                .getInputStreamParams()
                                .get(0)
                                .getInName()), "stream1"))
                .add(FilterStream.create(fixEventName(bindingParameters.getInputStreamParams().get(1).getInName()), "stream2"))); // in name

        logger.info("Generated EPL: " + model.toEPL());

        statements.add(model.toEPL());
        return statements;
    }

    private SelectClause makeSelectClause(BinaryMathParameter bindingParameters) {

        Operation selectedOperation = bindingParameters.getOperation();
        String asName = bindingParameters.getAppendPropertyName();

        SelectClause clause = SelectClause.create();
        for (String property : bindingParameters.getSelectProperties()) {
            clause.add(property);
        }

        Expression left = Expressions.property("stream1." +bindingParameters.getLeftOperand());
        Expression right = Expressions.property("stream2." +bindingParameters.getRightOperand());

        de.fzi.cep.sepa.esper.enrich.math.Math.mathExpression(selectedOperation, clause, left, right, asName);

        return clause;
    }

}
