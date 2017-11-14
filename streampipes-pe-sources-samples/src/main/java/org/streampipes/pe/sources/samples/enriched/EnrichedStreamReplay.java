package org.streampipes.pe.sources.samples.enriched;

import org.streampipes.commons.Utils;
import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.messaging.EventProducer;
import org.streampipes.messaging.kafka.SpKafkaProducer;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.vocabulary.MessageFormat;
import org.streampipes.pe.sources.samples.config.AkerVariables;
import org.streampipes.pe.sources.samples.config.ProaSenseSettings;
import org.streampipes.pe.sources.samples.config.SourcesConfig;

public class EnrichedStreamReplay implements EventStreamDeclarer {

	private String topicName;
	
	@Override
	public SpDataStream declareModel(DataSourceDescription sep) {
		SpDataStream stream = new SpDataStream();
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
		EventProducer producer = new SpKafkaProducer(SourcesConfig.INSTANCE.getKafkaUrl(), topicName);
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
