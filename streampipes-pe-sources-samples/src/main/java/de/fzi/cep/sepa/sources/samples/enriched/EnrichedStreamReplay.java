package de.fzi.cep.sepa.sources.samples.enriched;

import de.fzi.cep.sepa.client.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.messaging.EventProducer;
import de.fzi.cep.sepa.messaging.kafka.StreamPipesKafkaProducer;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;
import de.fzi.cep.sepa.sources.samples.config.AkerVariables;
import de.fzi.cep.sepa.sources.samples.config.ProaSenseSettings;

public class EnrichedStreamReplay implements EventStreamDeclarer {

	private String topicName;
	
	@Override
	public EventStream declareModel(SepDescription sep) {
		EventStream stream = new EventStream();
		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(ProaSenseSettings.standardProtocol("FZI.SEPA.SEP.Enriched.Replay"));
		grounding.setTransportFormats(de.fzi.cep.sepa.commons.Utils.createList(new TransportFormat(MessageFormat.Json)));
		
		this.topicName = grounding.getTransportProtocol().getTopicName();
		stream.setEventGrounding(grounding);
		stream.setEventSchema(EnrichedUtils.getEnrichedSchema());
		stream.setName("Enriched Stream Replay");
		stream.setDescription(AkerVariables.Enriched.description());
		stream.setUri(sep.getUri() + "/mhwirthenrichedreplay");

		return stream;
	}

	@Override
	public void executeStream() {
		EventProducer producer = new StreamPipesKafkaProducer(ClientConfiguration.INSTANCE.getKafkaUrl(), topicName);
		new Thread(new EnrichedReplay(producer)).start();
	}

	@Override
	public boolean isExecutable() {
		return true;
	}
	
	public static void main(String[] args) {
		EnrichedStreamReplay esr = new EnrichedStreamReplay();
		esr.topicName = "FZI.SEPA.SEP.Enriched.Replay";
		esr.executeStream();
	}

}
