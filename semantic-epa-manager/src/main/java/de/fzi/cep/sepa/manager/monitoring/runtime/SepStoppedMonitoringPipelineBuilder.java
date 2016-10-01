package de.fzi.cep.sepa.manager.monitoring.runtime;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import de.fzi.cep.sepa.commons.config.ConfigurationManager;
import de.fzi.cep.sepa.commons.exceptions.NoMatchingFormatException;
import de.fzi.cep.sepa.commons.exceptions.NoMatchingProtocolException;
import de.fzi.cep.sepa.commons.exceptions.NoMatchingSchemaException;
import de.fzi.cep.sepa.manager.matching.PipelineVerificationHandler;
import de.fzi.cep.sepa.manager.operations.Operations;
import de.fzi.cep.sepa.model.client.pipeline.PipelineModificationMessage;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.client.pipeline.Pipeline;

import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.*;
import de.fzi.cep.sepa.model.impl.staticproperty.DomainStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.SupportedProperty;
import de.fzi.cep.sepa.storage.controller.StorageManager;

public class SepStoppedMonitoringPipelineBuilder {

	// TODO make ULSs dynamic
//	private final String RATE_SEPA_URI = "http://frosch.fzi.de:8090/sepa/streamStopped";
//	private final String KAFKA_SEC_URI = "http://frosch.fzi.de:8091/kafka";

	private final String RATE_SEPA_URI = "http://ipe-koi05.perimeter.fzi.de:8090/sepa/streamStopped";
	private final String KAFKA_SEC_URI = "http://ipe-koi04.perimeter.fzi.de:8091/kafka";
	private final String OUTPUT_TOPIC = "internal.streamepipes.sec.stopped";

	private EventStream stream;
	private final String outputTopic;

	private SepDescription sepDescription;

	private SepaDescription streamStoppedSepaDescription;
	private SecDescription kafkaSecDescription;
	private String streamUri;

	public SepStoppedMonitoringPipelineBuilder(String sepUri, String streamUri) throws URISyntaxException {
		this.outputTopic = OUTPUT_TOPIC;
		this.streamUri = streamUri;
		SepDescription desc = StorageManager.INSTANCE.getStorageAPI().getSEPById(sepUri);
		this.stream = StorageManager.INSTANCE.getStorageAPI().getEventStreamById(streamUri);
		this.sepDescription = desc;
		this.streamStoppedSepaDescription = getStreamStoppedEpa();
		this.kafkaSecDescription = getKafkaPublisherEc();
	}

	public Pipeline buildPipeline()
			throws NoMatchingFormatException, NoMatchingSchemaException, NoMatchingProtocolException, Exception {
		SepaInvocation rateSepaClient = new SepaInvocation(streamStoppedSepaDescription);
		EventStream streamClient = new EventStream(stream);
		SecInvocation kafkaActionClient = new SecInvocation(kafkaSecDescription);

		List<NamedSEPAElement> elements = new ArrayList<>();
		elements.add(streamClient);

		rateSepaClient.setConnectedTo(Arrays.asList("stream"));
		streamClient.setDOM("stream");
		rateSepaClient.setDOM("rate");
		kafkaActionClient.setDOM("kafka");

		Pipeline pipeline = new Pipeline();
		pipeline.setStreams(Arrays.asList(streamClient));

		pipeline.setSepas(Arrays.asList(rateSepaClient));

		PipelineModificationMessage message = new PipelineVerificationHandler(pipeline).validateConnection()
				.computeMappingProperties().getPipelineModificationMessage();

		SepaInvocation updatedSepa = updateStreamStoppedSepa(rateSepaClient, message);
		pipeline.setSepas(Arrays.asList(updatedSepa));

		kafkaActionClient.setConnectedTo(Arrays.asList("rate"));
		pipeline.setActions(Arrays.asList(kafkaActionClient));

		message = new PipelineVerificationHandler(pipeline).validateConnection().computeMappingProperties()
				.getPipelineModificationMessage();

		pipeline.setActions(Arrays.asList(updateKafkaSec(kafkaActionClient, message)));

		pipeline.setPipelineId(UUID.randomUUID().toString());
		pipeline.setName("Monitoring - " + stream.getName());

		return pipeline;
	}

	private SecDescription getKafkaPublisherEc() throws URISyntaxException {
		return StorageManager.INSTANCE.getStorageAPI().getSECById(KAFKA_SEC_URI);
	}

	private SepaDescription getStreamStoppedEpa() throws URISyntaxException {
		return StorageManager.INSTANCE.getStorageAPI().getSEPAById(RATE_SEPA_URI);
	}

	private SecInvocation updateKafkaSec(SecInvocation actionClient, PipelineModificationMessage message) {
		List<StaticProperty> properties = message.getPipelineModifications().get(0).getStaticProperties();
		List<StaticProperty> newStaticProperties = new ArrayList<>();
		for (StaticProperty p : properties) {
			if (p instanceof FreeTextStaticProperty ||p instanceof DomainStaticProperty) {
				if (p instanceof FreeTextStaticProperty) {
					if (p.getInternalName().equals("topic"))
						((FreeTextStaticProperty) p).setValue(outputTopic);
				}
				else if (p instanceof DomainStaticProperty) {
					for(SupportedProperty sp : ((DomainStaticProperty) p).getSupportedProperties()) {
						if (sp.getPropertyId().equals("http://schema.org/kafkaHost"))
							sp.setValue(String
								.valueOf(ConfigurationManager.getWebappConfigurationFromProperties().getKafkaHost()));
						else if (sp.getPropertyId().equals("http://schema.org/kafkaPort"))
							sp.setValue(String
									.valueOf(ConfigurationManager.getWebappConfigurationFromProperties().getKafkaPort()));
					}
				}
					
			}
			newStaticProperties.add(p);
		}
		actionClient.setStaticProperties(newStaticProperties);
		return actionClient;
	}

	private SepaInvocation updateStreamStoppedSepa(SepaInvocation newSEPA, PipelineModificationMessage message) {
		List<StaticProperty> properties = message.getPipelineModifications().get(0).getStaticProperties();
		List<StaticProperty> newStaticProperties = new ArrayList<>();
		for (StaticProperty p : properties) {
			if (p instanceof FreeTextStaticProperty) {

					if (p.getInternalName().equals("topic"))
						((FreeTextStaticProperty) p).setValue(String.valueOf(streamUri));

			}
			newStaticProperties.add(p);
		}
		newSEPA.setStaticProperties(newStaticProperties);
		return newSEPA;
	}

	public static void main(String[] args) throws URISyntaxException {

		String SEP_URI = "http://frosch.fzi.de:8089//source-wunderbar";
		String STREAM_URI = "http://frosch.fzi.de:8089//source-wunderbar/accelerometer";

		SepStoppedMonitoringPipelineBuilder pc = new SepStoppedMonitoringPipelineBuilder(SEP_URI, STREAM_URI);

		try {
			Pipeline pipeline = pc.buildPipeline();
			Operations.startPipeline(pipeline, false, false, false);

			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String s = br.readLine();

			Operations.stopPipeline(pipeline, false, false, false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
