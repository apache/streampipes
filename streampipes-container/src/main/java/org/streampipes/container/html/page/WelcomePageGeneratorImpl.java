package org.streampipes.container.html.page;

import org.streampipes.container.declarer.Declarer;
import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.container.declarer.InvocableDeclarer;
import org.streampipes.container.declarer.SemanticEventConsumerDeclarer;
import org.streampipes.container.html.model.Description;
import org.streampipes.container.html.model.SemanticEventProducerDescription;
import org.streampipes.model.impl.graph.SecDescription;
import org.streampipes.container.declarer.SemanticEventProcessingAgentDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class WelcomePageGeneratorImpl extends WelcomePageGenerator<Declarer> {


    public WelcomePageGeneratorImpl(String baseUri, List<Declarer> declarers) {
        super(baseUri, declarers);
    }

    @Override
    public List<Description> buildUris() {
        List<Description> descriptions = new ArrayList<>();

        for (Declarer declarer : declarers) {
            if (declarer instanceof InvocableDeclarer) {
                descriptions.add(getDescription((InvocableDeclarer) declarer));
            } else if (declarer instanceof SemanticEventProducerDeclarer) {
                descriptions.add(getDescription((SemanticEventProducerDeclarer) declarer));
            }
        }
        return descriptions;
    }

    private Description getDescription(Declarer declarer) {
        Description desc = new Description();
        desc.setName(declarer.declareModel().getName());
        desc.setDescription(declarer.declareModel().getDescription());
        desc.setType(getType(declarer));
        String uri = baseUri;
        if (declarer instanceof SemanticEventConsumerDeclarer) {
            uri += "sec/";
        } else if (declarer instanceof SemanticEventProcessingAgentDeclarer) {
            uri += "sepa/";
        }
        desc.setUri(URI.create(uri +declarer.declareModel().getUri().replaceFirst("[a-zA-Z]{4}://[a-zA-Z\\.]+:\\d+/", "")));
        return desc;
    }

    private String getType(Declarer declarer) {
        if (declarer.declareModel() instanceof SecDescription) return "action";
        else return "sepa";
    }

    private Description getDescription(SemanticEventProducerDeclarer declarer) {
        List<Description> streams = new ArrayList<>();
        SemanticEventProducerDescription desc = new SemanticEventProducerDescription();
        desc.setName(declarer.declareModel().getName());
        desc.setDescription(declarer.declareModel().getDescription());
        desc.setUri(URI.create(baseUri + "sep/" + declarer.declareModel().getUri()));
        desc.setType("source");
        for (EventStreamDeclarer streamDeclarer : declarer.getEventStreams()) {
            Description ad = new Description();
            ad.setDescription(streamDeclarer.declareModel(declarer.declareModel()).getDescription());
            ad.setUri(URI.create(baseUri +"stream/" + streamDeclarer.declareModel(declarer.declareModel()).getUri()));
            ad.setName(streamDeclarer.declareModel(declarer.declareModel()).getName());
            ad.setType("stream");
            streams.add(ad);
        }
        desc.setStreams(streams);
        return desc;
    }
}
