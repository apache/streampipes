package org.streampipes.pe.processors.esper.compose;

import org.streampipes.commons.Utils;
import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.output.KeepOutputStrategy;
import org.streampipes.model.output.OutputStrategy;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.pe.processors.esper.config.EsperConfig;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarerSingleton;

import java.util.ArrayList;
import java.util.List;

public class ComposeController extends StandaloneEventProcessorDeclarerSingleton<ComposeParameters> {

	@Override
	public DataProcessorDescription declareModel() {
		
		SpDataStream stream1 = new SpDataStream();
		SpDataStream stream2 = new SpDataStream();
		
		DataProcessorDescription desc = new DataProcessorDescription("compose", "Compose EPA", "");
		
		stream1.setUri(EsperConfig.serverUrl +"/" +Utils.getRandomString());
		stream2.setUri(EsperConfig.serverUrl +"/" +Utils.getRandomString());
		desc.addEventStream(stream1);
		desc.addEventStream(stream2);
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		strategies.add(new KeepOutputStrategy());
		desc.setOutputStrategies(strategies);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		//staticProperties.add(new MatchingStaticProperty("select matching", ""));
		desc.setStaticProperties(staticProperties);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		return desc;
	}

	@Override
	public ConfiguredEventProcessor<ComposeParameters> onInvocation(DataProcessorInvocation
                                                                          sepa, ProcessingElementParameterExtractor extractor) {

		ComposeParameters staticParam = new ComposeParameters(sepa);

		return new ConfiguredEventProcessor<>(staticParam, Compose::new);
	}

}
