package de.fzi.cep.sepa.esper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import de.fzi.cep.sepa.desc.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;
import de.fzi.cep.sepa.runtime.EPEngine;
import de.fzi.cep.sepa.runtime.EPRuntime;
import de.fzi.cep.sepa.runtime.param.BindingParameters;
import de.fzi.cep.sepa.runtime.param.CamelConfig;
import de.fzi.cep.sepa.runtime.param.DataType;
import de.fzi.cep.sepa.runtime.param.EndpointInfo;
import de.fzi.cep.sepa.runtime.param.EngineParameters;
import de.fzi.cep.sepa.runtime.param.RuntimeParameters;

public abstract class EsperDeclarer<B extends BindingParameters> implements SemanticEventProcessingAgentDeclarer {

	public static final Logger logger = LoggerFactory.getLogger(EsperDeclarer.class.getCanonicalName());

	public static final Gson parser = new Gson();

	public static final CamelContext context = new DefaultCamelContext(); // routing context
	
	public Map<String, String> brokerAliases = new HashMap<String, String>();
	
	protected EPRuntime runtime;
	
	public boolean invokeEPRuntime(B bindingParameters, Supplier<EPEngine<B>> supplier, SEPAInvocationGraph sepa)
	{
		try {
					
			EndpointInfo destination;
			List<CamelConfig> config = new ArrayList<CamelConfig>();
			List<EndpointInfo> source = new ArrayList<EndpointInfo>();
			Map<String, Map<String, Object>> inEventTypes = new HashMap<>();
			Map<String, Object> outEventType = new HashMap<>();
			EngineParameters<B> engineParams;
			String outputBrokerAlias;
			
			EventGrounding outputEventGrounding = sepa.getOutputStream().getEventGrounding();
			String outputBrokerUrl = outputEventGrounding.getUri()+":" +outputEventGrounding.getPort();
			logger.info("OutputBrokerUrl is " +outputBrokerUrl);
			if (brokerAliases.containsKey(outputBrokerUrl)) outputBrokerAlias = brokerAliases.get(outputBrokerUrl);
			else
			{
				outputBrokerAlias = RandomStringUtils.randomAlphabetic(10);
				config.add(new CamelConfig.ActiveMQ(outputBrokerAlias, outputBrokerUrl));
				brokerAliases.put(outputBrokerUrl, outputBrokerAlias);
			}
			
			destination = EndpointInfo.of(outputBrokerAlias + ":topic:" +outputEventGrounding.getTopicName(), DataType.JSON);
			
			outEventType = sepa.getOutputStream().getEventSchema().toRuntimeMap();
			
			for(EventStream stream : sepa.getInputStreams())
			{
				EventGrounding inputEventGrounding = stream.getEventGrounding();
				String inputBrokerUrl = inputEventGrounding.getUri()+":" +inputEventGrounding.getPort();
				String inputBrokerAlias;
				
				if (brokerAliases.containsKey(inputBrokerUrl)) inputBrokerAlias = brokerAliases.get(inputBrokerUrl);
				else
				{
					inputBrokerAlias = RandomStringUtils.randomAlphabetic(10);
					config.add(new CamelConfig.ActiveMQ(inputBrokerAlias, inputBrokerUrl));
					brokerAliases.put(inputBrokerUrl, inputBrokerAlias);
				}
				source.add(EndpointInfo.of(inputBrokerAlias + ":topic:" +inputEventGrounding.getTopicName(), DataType.JSON));
				inEventTypes.put("topic://" +inputEventGrounding.getTopicName(), stream.getEventSchema().toRuntimeMap());
			}
			
			engineParams = new EngineParameters<>(
				inEventTypes,
				outEventType, bindingParameters, sepa);
	
			RuntimeParameters<B> runtimeParameters = new RuntimeParameters<>(sepa.getUri(),
					supplier, engineParams, config, destination, source);
			//context.getManagementStrategy().addEventNotifier(new LoggingEventNotifer());
			runtime = new EPRuntime(context, runtimeParameters);
			
			context.start();
			
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean detachRuntime() 
	{
		brokerAliases.clear();
		runtime.discard();
		return true;
	}
}
