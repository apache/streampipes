package org.streampipes.model.client.endpoint;

import java.util.List;

/**
 * Created by riemer on 05.10.2016.
 */
public class RdfEndpointItem {

    private String name;
    private String description;
    private String uri;
    private String type;
    private boolean installed;

    private List<RdfEndpointItem> streams;

    public RdfEndpointItem() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public List<RdfEndpointItem> getStreams() {
        return streams;
    }

    public void setStreams(List<RdfEndpointItem> streams) {
        this.streams = streams;
    }

    public boolean isInstalled() {
        return installed;
    }

    public void setInstalled(boolean installed) {
        this.installed = installed;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
