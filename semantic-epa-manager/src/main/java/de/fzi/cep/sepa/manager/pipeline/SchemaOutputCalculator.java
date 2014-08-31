package de.fzi.cep.sepa.manager.pipeline;


import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SEPA;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.CustomOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.FixedOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.output.RenameOutputStrategy;

public class SchemaOutputCalculator {

	public static EventSchema calculateOutputSchema(EventStream stream)
	{
		return stream.getEventSchema();
	}
	
	public static EventSchema calculateOutputSchema(EventStream outputStream, List<OutputStrategy> strategies)
	{
		EventSchema outputSchema = outputStream.getEventSchema();
		for(OutputStrategy strategy : strategies)
		{
			if (strategy instanceof AppendOutputStrategy)
			{
				AppendOutputStrategy thisStrategy = (AppendOutputStrategy) strategy;
				List<EventProperty> properties = thisStrategy.getEventProperties();
				properties.addAll(outputSchema.getEventProperties());
				return generateSchema(properties);
			}
			else if (strategy instanceof RenameOutputStrategy)
			{
				return outputSchema;
			}
			else if (strategy instanceof FixedOutputStrategy)
			{
				FixedOutputStrategy thisStrategy = (FixedOutputStrategy) strategy;
				return generateSchema(thisStrategy.getEventProperties());
			}
			else if (strategy instanceof CustomOutputStrategy)
			{
				CustomOutputStrategy thisStrategy = (CustomOutputStrategy) strategy;
				return generateSchema(thisStrategy.getEventProperties());
			}
		}
		// TODO exceptions
		return null;
	}
	
	public static EventSchema calculateOutputSchema(EventStream stream1, EventStream stream2, List<OutputStrategy> strategies)
	{
		for(OutputStrategy strategy : strategies)
		{
			if (strategy instanceof CustomOutputStrategy)
			{
				List<EventProperty> properties = stream1.getEventSchema().getEventProperties();
				properties.addAll(stream2.getEventSchema().getEventProperties());
				return generateSchema(properties);
			} 
		}
		//TODO exceptions
		return null;
	}
	
	private static EventSchema generateSchema(List<EventProperty> properties)
	{
		EventSchema result = new EventSchema();
		for(EventProperty p : properties)
			result.addEventProperty(p);
		return result;
	}
	
	
}
