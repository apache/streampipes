package org.streampipes.manager.matching.output;

import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.output.AppendOutputStrategy;
import org.streampipes.model.impl.output.OutputStrategy;

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
