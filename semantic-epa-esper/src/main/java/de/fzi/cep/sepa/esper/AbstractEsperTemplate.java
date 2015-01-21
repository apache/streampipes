package de.fzi.cep.sepa.esper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import de.fzi.cep.sepa.desc.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SEPA;
import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;
import de.fzi.cep.sepa.runtime.EPEngine;
import de.fzi.cep.sepa.runtime.EPRuntime;
import de.fzi.cep.sepa.runtime.param.BindingParameters;
import de.fzi.cep.sepa.runtime.param.CamelConfig;
import de.fzi.cep.sepa.runtime.param.DataType;
import de.fzi.cep.sepa.runtime.param.EndpointInfo;
import de.fzi.cep.sepa.runtime.param.EngineParameters;
import de.fzi.cep.sepa.runtime.param.OutputStrategy;
import de.fzi.cep.sepa.runtime.param.RuntimeParameters;

public abstract class AbstractEsperTemplate<B extends BindingParameters> implements SemanticEventProcessingAgentDeclarer {

	public static final Logger logger = LoggerFactory.getLogger("FilterTextTest");

	public static final Gson parser = new Gson();

	public static final CamelContext context = new DefaultCamelContext(); // routing context

	//public static final String BROKER_ALIAS = "test-jms";
	
	protected EPRuntime runtime;
	
	public abstract SEPA declareModel();

	protected boolean bind(B bindingParameters, Supplier<EPEngine<B>> supplier, SEPAInvocationGraph sepa) 
	{
		try {
			String BROKER_ALIAS = RandomStringUtils.randomAlphabetic(8);
			
			EndpointInfo destination;
			List<CamelConfig> config = new ArrayList<CamelConfig>();
			List<EndpointInfo> source = new ArrayList<EndpointInfo>();
			Map<String, Map<String, Class<?>>> inEventTypes = new HashMap<>();
		
			String baseEventName = sepa.getInputStreams().get(0).getName();
			EngineParameters<B> engineParams;
			
			destination = EndpointInfo.of(BROKER_ALIAS + ":topic:" +sepa.getOutputStream().getEventGrounding().getTopicName(), DataType.JSON);
			
			for(EventStream stream : sepa.getInputStreams())
			{
				config.add(new CamelConfig.ActiveMQ(BROKER_ALIAS, stream.getEventGrounding().getUri()+":" +stream.getEventGrounding().getPort()));
				source.add(EndpointInfo.of(BROKER_ALIAS + ":topic:" +stream.getEventGrounding().getTopicName(), DataType.JSON));
			
				inEventTypes.put("topic://" +stream.getEventGrounding().getTopicName(), stream.getEventSchema().toRuntimeMap());
				baseEventName = stream.getName();
			}
			
			engineParams = new EngineParameters<>(
				inEventTypes,
				new OutputStrategy.Rename(baseEventName, baseEventName), bindingParameters);
	
			RuntimeParameters<B> runtimeParameters = new RuntimeParameters<>(sepa.getUri(),
					supplier, engineParams, config, destination, source);
			
			runtime = new EPRuntime(context, runtimeParameters);
			/*
			context.addRoutes(new RouteBuilder() {
				@Override
				public void configure() throws Exception {
					from(BROKER_ALIAS +":topic:" +sepa.getOutputStream().getEventGrounding().getTopicName()) // .to("file://test")
					.process(ex -> logger.info("Receiving Event: {}", new String((byte[]) ex.getIn().getBody())));
				}
			});*/
	
			ProducerTemplate template = context.createProducerTemplate();
			template.setDefaultEndpointUri(BROKER_ALIAS + ":topic:PositionTopic");
	
			context.start();
			
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}	
}
