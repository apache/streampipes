package de.fzi.cep.sepa.actions.slack.sep;

import de.fzi.cep.sepa.client.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.client.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;

import java.util.Arrays;
import java.util.List;

public class SlackProducer implements SemanticEventProducerDeclarer {

    @Override
    public List<EventStreamDeclarer> getEventStreams() {
        return Arrays.asList(new SlackStream("random", "description"));
    }

    @Override
    public SepDescription declareModel() {
        SepDescription sep = new SepDescription("slack", "Slack Source", "Consumes data from slack");
        return sep;
    }
}
