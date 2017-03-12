package de.fzi.cep.sepa.manager.matching.output;

import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.CustomOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.FixedOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.ListOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.output.RenameOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.ReplaceOutputStrategy;

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
		else if (firstOutputStrategy instanceof RenameOutputStrategy)
			return new RenameOutputSchemaGenerator((RenameOutputStrategy) firstOutputStrategy);
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
