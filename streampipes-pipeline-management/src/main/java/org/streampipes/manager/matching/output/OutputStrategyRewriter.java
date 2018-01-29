package org.streampipes.manager.matching.output;

import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.output.AppendOutputStrategy;
import org.streampipes.model.output.OutputStrategy;

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
