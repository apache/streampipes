package org.streampipes.pe.slack.sep;

import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.impl.graph.SepDescription;

import java.util.Arrays;
import java.util.List;

public class SlackProducer implements SemanticEventProducerDeclarer {

    @Override
    public List<EventStreamDeclarer> getEventStreams() {
        return Arrays.asList(new SlackStream("Slack Bot", "Reads all messages from slack"));
    }

    @Override
    public SepDescription declareModel() {
        SepDescription sep = new SepDescription("slack", "Slack Source", "Consumes data from slack");
        return sep;
    }
}
