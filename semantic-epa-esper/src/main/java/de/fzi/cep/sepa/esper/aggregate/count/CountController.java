package de.fzi.cep.sepa.esper.aggregate.count;

import de.fzi.cep.sepa.client.util.StandardTransportFormat;
import de.fzi.cep.sepa.esper.config.EsperConfig;
import de.fzi.cep.sepa.model.impl.EpaType;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.runtime.flat.declarer.FlatEpDeclarer;
import de.fzi.cep.sepa.sdk.builder.ProcessingElementBuilder;
import de.fzi.cep.sepa.sdk.helpers.EpProperties;
import de.fzi.cep.sepa.sdk.helpers.EpRequirements;
import de.fzi.cep.sepa.sdk.helpers.Options;
import de.fzi.cep.sepa.sdk.helpers.OutputStrategies;

import java.util.ArrayList;
import java.util.List;

public class CountController extends FlatEpDeclarer<CountParameter>{

	@Override
	public SepaDescription declareModel() {

		return ProcessingElementBuilder.create("count", "Count Aggregation",
						"Performs an aggregation based on a given event property and outputs the number of occurrences.")
						.category(EpaType.AGGREGATE)
						.iconUrl(EsperConfig.iconBaseUrl +"/Counter_Icon_HQ.png")
//						.requiredPropertyStream1(EpRequirements.anyProperty())
                		.setStream1()
						.naryMappingPropertyWithoutRequirement("groupBy", "Group Stream By", "")
						.outputStrategy(OutputStrategies.append(EpProperties.integerEp("countValue",
										"http://schema.org/Number")))
						.requiredIntegerParameter("timeWindow", "Time Window Size", "Size of the time window " +
										"in seconds")
						.requiredSingleValueSelection("scale", "Time Window Scale", "",
										Options.from("Hours", "Minutes", "Seconds"))
						.supportedFormats(StandardTransportFormat.standardFormat())
						.supportedProtocols(StandardTransportFormat.standardProtocols())
						.build();
	}

	@Override
	public Response invokeRuntime(SepaInvocation sepa) {		
		
		List<String> groupBy = SepaUtils.getMultipleMappingPropertyNames(sepa,
				"groupBy", true);
		
		int timeWindowSize = Integer.parseInt(((FreeTextStaticProperty) (SepaUtils
				.getStaticPropertyByInternalName(sepa, "timeWindow"))).getValue());
	
		String scale = SepaUtils.getOneOfProperty(sepa,
				"scale");
		
		TimeScale timeScale;
		
		if (scale.equals("Hours")) timeScale = TimeScale.HOURS;
		else if (scale.equals("Minutes")) timeScale = TimeScale.MINUTES;
		else timeScale = TimeScale.SECONDS;
		
		List<String> selectProperties = new ArrayList<>();
		for(EventProperty p : sepa.getInputStreams().get(0).getEventSchema().getEventProperties())
		{
			selectProperties.add(p.getRuntimeName());
		}
		
		CountParameter staticParam = new CountParameter(sepa, timeWindowSize, groupBy, timeScale, selectProperties);

		return submit(staticParam, Count::new, sepa);
	}
}
