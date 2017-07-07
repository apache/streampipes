package org.streampipes.pe.sources.samples.enriched;

import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.commons.Utils;
import org.streampipes.commons.config.ClientConfiguration;
import org.streampipes.messaging.EventProducer;
import org.streampipes.messaging.kafka.StreamPipesKafkaProducer;
import org.streampipes.model.impl.EventGrounding;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.model.vocabulary.MessageFormat;
import org.streampipes.pe.sources.samples.config.AkerVariables;
import org.streampipes.pe.sources.samples.config.ProaSenseSettings;

public class EnrichedStreamReplay implements EventStreamDeclarer {

	private String topicName;
	
	@Override
	public EventStream declareModel(SepDescription sep) {
		EventStream stream = new EventStream();
		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(ProaSenseSettings.standardProtocol("FZI.SEPA.SEP.Enriched.Replay"));
		grounding.setTransportFormats(Utils.createList(new TransportFormat(MessageFormat.Json)));
		
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
