package de.fzi.cep.sepa.client.html.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class SemanticEventProducerDescription extends Description {

    private List<Description> streams;

    public SemanticEventProducerDescription(String name, String description, URI uri, List<Description> streams) {
        super(name, description, uri);
        this.streams = streams;
    }

    public SemanticEventProducerDescription() {
        streams = new ArrayList<>();
    }

    public List<Description> getStreams() {
        return streams;
    }

    public void setStreams(List<Description> streams) {
        this.streams = streams;
    }
}
