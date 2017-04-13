package de.fzi.cep.sepa.flink.samples.enrich.configurabletimestamp;

import de.fzi.cep.sepa.client.util.StandardTransportFormat;
import de.fzi.cep.sepa.flink.AbstractFlinkAgentDeclarer;
import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import de.fzi.cep.sepa.flink.samples.Config;
import de.fzi.cep.sepa.model.impl.EpaType;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.quality.EventStreamQualityRequirement;
import de.fzi.cep.sepa.model.impl.quality.Frequency;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.sdk.builder.ProcessingElementBuilder;
import de.fzi.cep.sepa.sdk.extractor.ProcessingElementParameterExtractor;
import de.fzi.cep.sepa.sdk.helpers.*;

import java.util.ArrayList;
import java.util.List;

public class ConfigurableTimestampController extends AbstractFlinkAgentDeclarer<ConfigurableTimestampParameters>{

	@Override
	public SepaDescription declareModel() {
		return ProcessingElementBuilder.create("enrich_configurable_timestamp", "Configurable Flink Timestamp Enrichment",
				"Appends the current time in ms to the event payload using Flink")
				.category(EpaType.ENRICH)
				.requiredTextParameter("timestamp_name", "Timestamp Name", "The label that is used for the appended timestamp")
				.requiredIntegerParameter("blk", "sdf", "asdf")
				.requiredPropertyStream1(EpRequirements.anyProperty())
				.outputStrategy(OutputStrategies.append(
						EpProperties.longEp("appendedTime", SO.DateTime)))
				.supportedProtocols(SupportedProtocols.kafka())
				.supportedFormats(SupportedFormats.jsonFormat())
				.build();
	}

	@Override
	protected FlinkSepaRuntime<ConfigurableTimestampParameters> getRuntime(
			SepaInvocation graph) {

		ProcessingElementParameterExtractor extractor = ProcessingElementParameterExtractor.from(graph);

		String timestampName = extractor.mappingPropertyValue("timestamp_name");

		ConfigurableTimestampParameters staticParam = new ConfigurableTimestampParameters(
				graph,
				timestampName
		);

		return new ConfigurableTimestampProgram(staticParam, new FlinkDeploymentConfig(Config.JAR_FILE, Config.FLINK_HOST, Config.FLINK_PORT));
//		return new ConfigurableTimestampProgram(staticParam);
	}

}
