package de.fzi.cep.sepa.flink.samples.count.aggregate;

import de.fzi.cep.sepa.client.util.StandardTransportFormat;
import de.fzi.cep.sepa.flink.AbstractFlinkAgentDeclarer;
import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import de.fzi.cep.sepa.flink.samples.Config;
import de.fzi.cep.sepa.model.impl.EpaType;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.Geo;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.sdk.builder.ProcessingElementBuilder;
import de.fzi.cep.sepa.sdk.extractor.ProcessingElementParameterExtractor;
import de.fzi.cep.sepa.sdk.helpers.EpProperties;
import de.fzi.cep.sepa.sdk.helpers.Options;
import de.fzi.cep.sepa.sdk.helpers.OutputStrategies;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.util.List;

public class CountAggregateController extends AbstractFlinkAgentDeclarer<CountAggregateParameters>{


	@Override
	public SepaDescription declareModel() {
		return ProcessingElementBuilder.create("taxi_aggregare_count", "Flink Taxi Count Aggregation",
				"Performs an aggregation based on taxi data")
				.category(EpaType.AGGREGATE)
				.setStream1()
				.naryMappingPropertyWithoutRequirement(CountAggregateConstants.GROUP_BY, "Group Stream By", "")
				.outputStrategy(
						OutputStrategies.fixed(
								EpProperties.integerEp(CountAggregateConstants.AGGREGATE_TAXI_COUNT, SO.Number),
								EpProperties.longEp(CountAggregateConstants.WINDOW_TIME_START, SO.DateTime),
								EpProperties.longEp(CountAggregateConstants.WINDOW_TIME_END, SO.DateTime),
								EpProperties.integerEp(CountAggregateConstants.PASSENGER_COUNT_AVG, SO.Number),
								EpProperties.doubleEp(CountAggregateConstants.TRIP_DISTANCE_AVG, SO.Number),
								EpProperties.doubleEp(CountAggregateConstants.EXTRA_AVG, SO.Number),
								EpProperties.doubleEp(CountAggregateConstants.TIP_AMOUNT_AVG, SO.Number),
								EpProperties.doubleEp(CountAggregateConstants.TOLLS_AMOUNT_AVG, SO.Number),
								EpProperties.doubleEp(CountAggregateConstants.FARE_AMOUNT_AVG, SO.Number),
								EpProperties.doubleEp(CountAggregateConstants.TOTAL_AMOUNT_AVG, SO.Number),
								EpProperties.integerEp(CountAggregateConstants.RATE_CODE_ID_1, SO.Number),
								EpProperties.integerEp(CountAggregateConstants.RATE_CODE_ID_2, SO.Number),
								EpProperties.integerEp(CountAggregateConstants.RATE_CODE_ID_3, SO.Number),
								EpProperties.integerEp(CountAggregateConstants.RATE_CODE_ID_4, SO.Number),
								EpProperties.integerEp(CountAggregateConstants.RATE_CODE_ID_5, SO.Number),
								EpProperties.integerEp(CountAggregateConstants.RATE_CODE_ID_6, SO.Number),
								EpProperties.integerEp(CountAggregateConstants.PAYMENT_TYPE_1, SO.Number),
								EpProperties.integerEp(CountAggregateConstants.PAYMENT_TYPE_2, SO.Number),
								EpProperties.integerEp(CountAggregateConstants.PAYMENT_TYPE_3, SO.Number),
								EpProperties.integerEp(CountAggregateConstants.PAYMENT_TYPE_4, SO.Number),
								EpProperties.integerEp(CountAggregateConstants.PAYMENT_TYPE_5, SO.Number),
								EpProperties.integerEp(CountAggregateConstants.PAYMENT_TYPE_6, SO.Number),
								EpProperties.integerEp(CountAggregateConstants.MTA_TAX, SO.Number),
								EpProperties.integerEp(CountAggregateConstants.IMPROVEMENT_SURCHARGE, SO.Number),

								EpProperties.doubleEp(CountAggregateConstants.GRID_LAT_NW_KEY, Geo.lat),
                    			EpProperties.doubleEp(CountAggregateConstants.GRID_LON_NW_KEY, Geo.lng),
                    			EpProperties.doubleEp(CountAggregateConstants.GRID_LAT_SE_KEY, Geo.lat),
                    			EpProperties.doubleEp(CountAggregateConstants.GRID_LON_SE_KEY, Geo.lng),
                                EpProperties.stringEp(CountAggregateConstants.GRID_CELL_ID, SO.Text)
								))
				.requiredIntegerParameter(CountAggregateConstants.TIME_WINDOW, "Time Window Size", "Size of the time window")
				.requiredIntegerParameter(CountAggregateConstants.SLIDE_WINDOW, "Slide Window Size", "Time how much the window should slide")
				.requiredSingleValueSelection(CountAggregateConstants.SCALE, "Time Window Scale", "",
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
				CountAggregateConstants.GROUP_BY, true);

		int timeWindowSize = extractor.singleValueParameter(CountAggregateConstants.TIME_WINDOW, Integer.class);
		int slidingWindowSize = extractor.singleValueParameter(CountAggregateConstants.SLIDE_WINDOW, Integer.class);

		String scale = SepaUtils.getOneOfProperty(sepa, CountAggregateConstants.SCALE);

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

		return new CountAggregateProgram(staticParam, new FlinkDeploymentConfig(Config.JAR_FILE, Config.FLINK_HOST, Config.FLINK_PORT));
//		return new CountAggregateProgram(staticParam);
	}

}
