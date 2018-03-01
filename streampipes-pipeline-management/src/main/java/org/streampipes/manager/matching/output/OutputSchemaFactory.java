package org.streampipes.manager.matching.output;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.output.AppendOutputStrategy;
import org.streampipes.model.output.CustomOutputStrategy;
import org.streampipes.model.output.FixedOutputStrategy;
import org.streampipes.model.output.KeepOutputStrategy;
import org.streampipes.model.output.ListOutputStrategy;
import org.streampipes.model.output.OutputStrategy;
import org.streampipes.model.output.TransformOutputStrategy;

public class OutputSchemaFactory {

	private OutputStrategy firstOutputStrategy;
	private DataProcessorInvocation dataProcessorInvocation;
	
	public OutputSchemaFactory(DataProcessorInvocation dataProcessorInvocation)
	{
		this.dataProcessorInvocation = dataProcessorInvocation;
		this.firstOutputStrategy = dataProcessorInvocation.getOutputStrategies().get(0);
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
		else if (firstOutputStrategy instanceof TransformOutputStrategy) {
			return new TransformOutputSchemaGenerator(dataProcessorInvocation, (TransformOutputStrategy) firstOutputStrategy);
		} else {
			throw new IllegalArgumentException();
		}
	}
}
