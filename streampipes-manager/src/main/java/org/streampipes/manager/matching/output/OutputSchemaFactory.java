package org.streampipes.manager.matching.output;

import org.streampipes.model.output.AppendOutputStrategy;
import org.streampipes.model.output.CustomOutputStrategy;
import org.streampipes.model.output.FixedOutputStrategy;
import org.streampipes.model.output.ListOutputStrategy;
import org.streampipes.model.output.OutputStrategy;
import org.streampipes.model.output.KeepOutputStrategy;
import org.streampipes.model.output.ReplaceOutputStrategy;

import java.util.List;

public class OutputSchemaFactory {

	private OutputStrategy firstOutputStrategy;
	
	public OutputSchemaFactory(List<OutputStrategy> outputStrategies)
	{
		this.firstOutputStrategy = outputStrategies.get(0);
	}
	
	public OutputSchemaGenerator<?> getOuputSchemaGenerator()
	{
		if (firstOutputStrategy instanceof AppendOutputStrategy)
			return new AppendOutputSchemaGenerator(((AppendOutputStrategy) firstOutputStrategy).getEventProperties());
		else if (firstOutputStrategy instanceof KeepOutputStrategy)
			return new RenameOutputSchemaGenerator((KeepOutputStrategy) firstOutputStrategy);
		else if (firstOutputStrategy instanceof FixedOutputStrategy)
			return new FixedOutputSchemaGenerator(((FixedOutputStrategy) firstOutputStrategy).getEventProperties());
		else if (firstOutputStrategy instanceof CustomOutputStrategy)
			return new CustomOutputSchemaGenerator(((CustomOutputStrategy) firstOutputStrategy).getEventProperties());
		else if (firstOutputStrategy instanceof ListOutputStrategy)
			return new ListOutputSchemaGenerator(((ListOutputStrategy) firstOutputStrategy).getPropertyName());
		else if (firstOutputStrategy instanceof ReplaceOutputStrategy)
			return new ReplaceOutputSchemaGenerator((ReplaceOutputStrategy) firstOutputStrategy);
		else throw new IllegalArgumentException();
	}
}
