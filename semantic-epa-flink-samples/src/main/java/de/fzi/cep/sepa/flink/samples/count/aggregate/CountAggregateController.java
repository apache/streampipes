package de.fzi.cep.sepa.flink.samples.count.aggregate;

import de.fzi.cep.sepa.client.util.StandardTransportFormat;
import de.fzi.cep.sepa.flink.AbstractFlinkAgentDeclarer;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import de.fzi.cep.sepa.model.impl.EpaType;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.sdk.builder.ProcessingElementBuilder;
import de.fzi.cep.sepa.sdk.extractor.ProcessingElementParameterExtractor;
import de.fzi.cep.sepa.sdk.helpers.EpProperties;
import de.fzi.cep.sepa.sdk.helpers.Options;
import de.fzi.cep.sepa.sdk.helpers.OutputStrategies;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.util.ArrayList;
import java.util.List;

public class CountAggregateController extends AbstractFlinkAgentDeclarer<CountAggregateParameters>{

	@Override
	public SepaDescription declareModel() {
		return ProcessingElementBuilder.create("count", "Flink Taxi Count Aggregation",
				"Performs an aggregation based on a given event property and outputs the number of occurrences.")
				.category(EpaType.AGGREGATE)
//						.iconUrl("")
				.setStream1()
				.naryMappingPropertyWithoutRequirement("groupBy", "Group Stream By", "")
				.outputStrategy(OutputStrategies.append(EpProperties.integerEp("countValue",
						"http://schema.org/Number")))
				.requiredIntegerParameter("timeWindow", "Time Window Size", "Size of the time window")
				.requiredIntegerParameter("slideWindow", "Slide Window Size", "Time how much the window should slide")
				.requiredSingleValueSelection("scale", "Time Window Scale", "",
						Options.from("Hours", "Minutes", "Seconds"))
				.supportedFormats(StandardTransportFormat.standardFormat())
				.supportedProtocols(StandardTransportFormat.standardProtocols())
				.build();

	}

	@Override
	protected FlinkSepaRuntime<CountAggregateParameters> getRuntime(
			SepaInvocation sepa) {
		ProcessingElementParameterExtractor extractor = ProcessingElementParameterExtractor.from(sepa);

		List<String> groupBy = SepaUtils.getMultipleMappingPropertyNames(sepa,
				"groupBy", true);

		int timeWindowSize = extractor.singleValueParameter("timeWindow", Integer.class);
		int slidingWindowSize = extractor.singleValueParameter("slideWindow", Integer.class);

		String scale = SepaUtils.getOneOfProperty(sepa,
				"scale");

		Time timeWindow;
		Time slideWindow;

		if (scale.equals("Hours")) {
			timeWindow = Time.hours(timeWindowSize);
			slideWindow = Time.hours(slidingWindowSize);
		}
		else if (scale.equals("Minutes")) {
			timeWindow = Time.minutes(timeWindowSize);
			slideWindow = Time.minutes(slidingWindowSize);
		}
		else {
	    	timeWindow = Time.seconds(timeWindowSize);
			slideWindow = Time.seconds(slidingWindowSize);
		}


		CountAggregateParameters staticParam = new CountAggregateParameters(sepa, timeWindow, slideWindow, groupBy);

//		return new CountAggregateProgram(staticParam, new FlinkDeploymentConfig(Config.JAR_FILE, Config.FLINK_HOST, Config.FLINK_PORT));
		return new CountAggregateProgram(staticParam);
	}

}
