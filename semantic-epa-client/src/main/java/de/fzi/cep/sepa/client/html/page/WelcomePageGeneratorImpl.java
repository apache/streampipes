package de.fzi.cep.sepa.client.html.page;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.declarer.Declarer;
import de.fzi.cep.sepa.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.declarer.InvocableDeclarer;
import de.fzi.cep.sepa.declarer.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.declarer.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.html.model.Description;
import de.fzi.cep.sepa.html.model.SemanticEventProducerDescription;

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
        String uri = baseUri;
        if (declarer instanceof SemanticEventConsumerDeclarer) {
            uri += "sec/";
        } else if (declarer instanceof SemanticEventProcessingAgentDeclarer) {
            uri += "sepa/";
        }
        desc.setUri(URI.create(uri +declarer.declareModel().getUri().replaceFirst("[a-zA-Z]{4}://[a-zA-Z\\.]+:\\d+/", "")));
        return desc;
    }

    private Description getDescription(SemanticEventProducerDeclarer declarer) {
        List<Description> streams = new ArrayList<>();
        SemanticEventProducerDescription desc = new SemanticEventProducerDescription();
        desc.setName(declarer.declareModel().getName());
        desc.setDescription(declarer.declareModel().getDescription());
        desc.setUri(URI.create(baseUri + "sep/" + declarer.declareModel().getUri()));
        for (EventStreamDeclarer streamDeclarer : declarer.getEventStreams()) {
            Description ad = new Description();
            ad.setDescription(streamDeclarer.declareModel(declarer.declareModel()).getDescription());
            ad.setUri(URI.create(baseUri +"stream/" + streamDeclarer.declareModel(declarer.declareModel()).getUri()));
            ad.setName(streamDeclarer.declareModel(declarer.declareModel()).getName());
            streams.add(ad);
        }
        desc.setStreams(streams);
        return desc;
    }
}
