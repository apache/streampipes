package org.streampipes.pe.slack.sep;

import org.streampipes.container.declarer.DataStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.graph.DataSourceDescription;

import java.util.Arrays;
import java.util.List;

public class SlackProducer implements SemanticEventProducerDeclarer {

    @Override
    public List<DataStreamDeclarer> getEventStreams() {
        return Arrays.asList(new SlackStream("Slack Bot", "Reads all messages from slack"));
    }

    @Override
    public DataSourceDescription declareModel() {
        DataSourceDescription sep = new DataSourceDescription("slack", "Slack Source", "Consumes data from slack");
        return sep;
    }
}
