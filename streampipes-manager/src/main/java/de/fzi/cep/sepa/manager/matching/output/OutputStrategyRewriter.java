package de.fzi.cep.sepa.manager.matching.output;

import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;

public class OutputStrategyRewriter {

	public OutputStrategy rewrite(EventSchema outputSchema, OutputStrategy strategy)
	{
		if (!(strategy instanceof AppendOutputStrategy)) return strategy;
		else return updateAppendOutput(outputSchema, (AppendOutputStrategy) strategy);
	}

	private OutputStrategy updateAppendOutput(EventSchema outputSchema,
			AppendOutputStrategy strategy) {
		
		
		return strategy;
	}
}
