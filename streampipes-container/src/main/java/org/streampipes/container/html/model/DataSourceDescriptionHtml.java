package org.streampipes.container.html.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class DataSourceDescriptionHtml extends Description {

    private List<Description> streams;

    public DataSourceDescriptionHtml(String name, String description, URI uri, List<Description> streams) {
        super(name, description, uri);
        this.streams = streams;
    }

    public DataSourceDescriptionHtml() {
        streams = new ArrayList<>();
    }

    public List<Description> getStreams() {
        return streams;
    }

    public void setStreams(List<Description> streams) {
        this.streams = streams;
    }
}
