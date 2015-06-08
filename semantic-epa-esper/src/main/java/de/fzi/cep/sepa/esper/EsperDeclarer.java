package de.fzi.cep.sepa.esper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import de.fzi.cep.sepa.desc.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.JmsTransportProtocol;
import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;
import de.fzi.cep.sepa.model.impl.TransportProtocol;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;
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
	
	public Map<String, GroundingConfig> brokerAliases = new HashMap<>();
	
	protected EPRuntime runtime;
	
	public boolean invokeEPRuntime(B bindingParameters, Supplier<EPEngine<B>> supplier, SepaInvocation sepa)
	{
		try {
					
			EndpointInfo destination;
			List<CamelConfig> config = new ArrayList<CamelConfig>();
			List<EndpointInfo> source = new ArrayList<EndpointInfo>();
			Map<String, Map<String, Object>> inEventTypes = new HashMap<>();
			Map<String, Object> outEventType = new HashMap<>();
			EngineParameters<B> engineParams;
			GroundingConfig outputGroundingConfig;
			
			EventGrounding outputEventGrounding = sepa.getOutputStream().getEventGrounding();
			TransportProtocol outputProtocol = outputEventGrounding.getTransportProtocol();
			int outputPort = outputProtocol instanceof JmsTransportProtocol ? ((JmsTransportProtocol) outputProtocol).getPort() : ((KafkaTransportProtocol) outputProtocol).getKafkaPort();
			
			String outputBrokerUrl = outputEventGrounding.getTransportProtocol().getBrokerHostname()+":" +outputPort;
			logger.info("OutputBrokerUrl is " +outputBrokerUrl);
			if (brokerAliases.containsKey(outputBrokerUrl)) outputGroundingConfig = brokerAliases.get(outputBrokerUrl);
			else
			{
				outputGroundingConfig = new GroundingConfig(outputEventGrounding);
				config.add(outputGroundingConfig.getCamelConfig());
				brokerAliases.put(outputBrokerUrl, outputGroundingConfig);
			}
			
			destination = EndpointInfo.of(outputGroundingConfig.getEndpointUri(outputEventGrounding.getTransportProtocol().getTopicName()), getMessageFormat(outputEventGrounding));
			
			outEventType = sepa.getOutputStream().getEventSchema().toRuntimeMap();
			
			for(EventStream stream : sepa.getInputStreams())
			{
				EventGrounding inputEventGrounding = stream.getEventGrounding();
				TransportProtocol protocol = inputEventGrounding.getTransportProtocol();
				int port = protocol instanceof JmsTransportProtocol ? ((JmsTransportProtocol) protocol).getPort() : ((KafkaTransportProtocol) protocol).getKafkaPort();
				String inputBrokerUrl = inputEventGrounding.getTransportProtocol().getBrokerHostname()+":" +port;
				GroundingConfig inputGroundingConfig;
				
				if (brokerAliases.containsKey(inputBrokerUrl)) inputGroundingConfig = brokerAliases.get(inputBrokerUrl);
				else
				{
					inputGroundingConfig = new GroundingConfig(inputEventGrounding);
					config.add(inputGroundingConfig.getCamelConfig());
					brokerAliases.put(inputBrokerUrl, inputGroundingConfig);
				}
				source.add(EndpointInfo.of(inputGroundingConfig.getEndpointUri(inputEventGrounding.getTransportProtocol().getTopicName()), getMessageFormat(inputEventGrounding)));
				inEventTypes.put("topic://" +inputEventGrounding.getTransportProtocol().getTopicName(), stream.getEventSchema().toRuntimeMap());
				stream.getEventSchema().toRuntimeMap().keySet().forEach(key -> System.out.print(key +", " +stream.getEventSchema().toRuntimeMap().get(key) +", "));
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
	
	private DataType getMessageFormat(EventGrounding inputEventGrounding) {
		if (inputEventGrounding.getTransportFormats().get(0).getRdfType().stream().anyMatch(type -> type.toString().equals(MessageFormat.Thrift))) return DataType.THRIFT;
		else return DataType.JSON;
	}
	
	public boolean detachRuntime() 
	{
		brokerAliases.clear();
		runtime.discard();
		return true;
	}
}
