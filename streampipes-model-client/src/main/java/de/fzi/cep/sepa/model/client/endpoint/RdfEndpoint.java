package de.fzi.cep.sepa.model.client.endpoint;

import com.google.gson.annotations.SerializedName;

/**
 * Created by riemer on 05.10.2016.
 */
public class RdfEndpoint {

    private @SerializedName("_id") String id;
    private @SerializedName("_rev") String rev;

    private String endpointUrl;

    public RdfEndpoint(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }
}
