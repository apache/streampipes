package de.fzi.cep.sepa.manager.monitoring.runtime;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.commons.config.ConfigurationManager;
import de.fzi.cep.sepa.commons.exceptions.NoMatchingFormatException;
import de.fzi.cep.sepa.commons.exceptions.NoMatchingProtocolException;
import de.fzi.cep.sepa.commons.exceptions.NoMatchingSchemaException;
import de.fzi.cep.sepa.manager.matching.PipelineValidationHandler;
import de.fzi.cep.sepa.manager.operations.Operations;
import de.fzi.cep.sepa.messages.PipelineModificationMessage;
import de.fzi.cep.sepa.model.client.ActionClient;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.model.client.SEPAClient;
import de.fzi.cep.sepa.model.client.SEPAElement;
import de.fzi.cep.sepa.model.client.StaticProperty;
import de.fzi.cep.sepa.model.client.StaticPropertyType;
import de.fzi.cep.sepa.model.client.StreamClient;
import de.fzi.cep.sepa.model.client.input.ElementType;
import de.fzi.cep.sepa.model.client.input.TextInput;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.cep.sepa.storage.util.ClientModelTransformer;
import de.fzi.sepa.model.client.util.Utils;

public class RateMonitoringPipelineBuilder {

	private final String RATE_SEPA_URI = ClientConfiguration.INSTANCE.getEsperUrl() +"/sepa/eventrate";
	private final String KAFKA_SEC_URI = ClientConfiguration.INSTANCE.getActionUrl() +"/kafka";
	
//	private final String RATE_SEPA_URI = "http://frosch.fzi.de:8090/sepa/eventrate";
//	private final String KAFKA_SEC_URI = "http://frosch.fzi.de:8091/kafka";
		
	private static final int RATE_AGG_SECS = 5;
	private static final int RATE_OUTPUT_SECS = 1;
	
	private EventStream stream;
	private final String outputTopic;
	
	private SepDescription sepDescription;
	
	private SepaDescription rateSepaDescription;
	private SecDescription kafkaSecDescription;
	
	
	public RateMonitoringPipelineBuilder(SepDescription sepDescription, EventStream stream, String outputTopic) throws URISyntaxException {
		System.out.println(RATE_SEPA_URI);
		this.stream = stream;
		this.outputTopic = outputTopic;
		this.sepDescription = sepDescription;
		this.rateSepaDescription = getRateEpa();
		this.kafkaSecDescription = getKafkaPublisherEc();
	}
	
	public Pipeline buildPipeline() throws NoMatchingFormatException, NoMatchingSchemaException, NoMatchingProtocolException, Exception {
		SEPAClient rateSepaClient = ClientModelTransformer.toSEPAClientModel(rateSepaDescription);
		StreamClient streamClient = ClientModelTransformer.toStreamClientModel(sepDescription, stream);
		ActionClient kafkaActionClient = ClientModelTransformer.toSECClientModel(kafkaSecDescription);
		
		List<SEPAElement> elements = new ArrayList<>();
		elements.add(streamClient);
		
		rateSepaClient.setConnectedTo(Arrays.asList("stream"));
		streamClient.setDOM("stream");
		rateSepaClient.setDOM("rate");
		kafkaActionClient.setDOM("kafka");
		
		Pipeline pipeline = new Pipeline();
		pipeline.setStreams(Arrays.asList(streamClient));
		
		pipeline.setSepas(Arrays.asList(rateSepaClient));
		
		PipelineModificationMessage message = new PipelineValidationHandler(pipeline, true)
			.validateConnection()
			.computeMappingProperties()
			.getPipelineModificationMessage();
		
		SEPAClient updatedSepa  = updateRateSepa(rateSepaClient, message);
		pipeline.setSepas(Arrays.asList(updatedSepa));
		
		kafkaActionClient.setConnectedTo(Arrays.asList("rate"));
		pipeline.setAction(kafkaActionClient);
		
		message = new PipelineValidationHandler(pipeline, false)
			.validateConnection()
			.computeMappingProperties()
			.getPipelineModificationMessage();	
		
		pipeline.setAction(updateKafkaSec(kafkaActionClient, message));
		
		pipeline.setPipelineId(UUID.randomUUID().toString());
		pipeline.setName("Monitoring - " +stream.getName());
		
		System.out.println(Utils.getGson().toJson(pipeline));
		
		return pipeline;
	}
	
	
	private SecDescription getKafkaPublisherEc() throws URISyntaxException {
		return StorageManager.INSTANCE.getStorageAPI().getSECById(KAFKA_SEC_URI);
	}
	
	private SepaDescription getRateEpa() throws URISyntaxException {
		return StorageManager.INSTANCE.getStorageAPI().getSEPAById(RATE_SEPA_URI);
	}
	
	private ActionClient updateKafkaSec(ActionClient actionClient,
			PipelineModificationMessage message) {
		List<StaticProperty> properties = message.getPipelineModifications().get(0).getStaticProperties();
		List<StaticProperty> newStaticProperties = new ArrayList<>();
		for(StaticProperty p : properties)
		{
			if (p.getType() == StaticPropertyType.STATIC_PROPERTY)
			{
				if (p.getInput().getElementType() == ElementType.TEXT_INPUT)
				{
					if (p.getInternalName().equals("hostname"))
						((TextInput) p.getInput()).setValue(String.valueOf(ConfigurationManager.getWebappConfigurationFromProperties().getKafkaHost()));
					else if (p.getInternalName().equals("port"))
						((TextInput) p.getInput()).setValue(String.valueOf(ConfigurationManager.getWebappConfigurationFromProperties().getKafkaPort()));
					else if (p.getInternalName().equals("topic"))
						((TextInput) p.getInput()).setValue(outputTopic);
				}			
			}
			newStaticProperties.add(p);
		}
		actionClient.setStaticProperties(newStaticProperties);
		return actionClient;
	}
	
	private SEPAClient updateRateSepa(SEPAClient newSEPA,
			PipelineModificationMessage message) {
		List<StaticProperty> properties = message.getPipelineModifications().get(0).getStaticProperties();
		List<StaticProperty> newStaticProperties = new ArrayList<>();
		for(StaticProperty p : properties)
		{
			if (p.getType() == StaticPropertyType.STATIC_PROPERTY)
			{
				
				if (p.getInput().getElementType() == ElementType.TEXT_INPUT)
				{
					if (p.getInternalName().equals("rate"))
						((TextInput) p.getInput()).setValue(String.valueOf(RATE_AGG_SECS));
					else if (p.getInternalName().equals("output"))
						((TextInput) p.getInput()).setValue(String.valueOf(RATE_OUTPUT_SECS));
				}
			}
			newStaticProperties.add(p);
		}
		newSEPA.setStaticProperties(newStaticProperties);
		return newSEPA;
	}
	
	public static void main(String[] args) throws URISyntaxException {
		SepDescription desc = StorageManager.INSTANCE.getStorageAPI().getSEPById("http://frosch.fzi.de:8080/sources-monitoring/source-monitoring");
		EventStream stream = desc.getEventStreams().get(0);
		RateMonitoringPipelineBuilder pc = new RateMonitoringPipelineBuilder(desc, stream, "abc");
			
		try {
			Pipeline pipeline = pc.buildPipeline();
			Operations.startPipeline(pipeline, false, false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
