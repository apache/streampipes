package org.streampipes.manager.endpoint;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.streampipes.model.client.endpoint.RdfEndpoint;
import org.streampipes.model.client.endpoint.RdfEndpointItem;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicHeader;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by riemer on 05.10.2016.
 */
public class EndpointItemFetcher {

    private List<RdfEndpoint> rdfEndpoints;

    public EndpointItemFetcher(List<RdfEndpoint> rdfEndpoints) {
        this.rdfEndpoints = rdfEndpoints;
    }

    public List<RdfEndpointItem> getItems() {
        List<RdfEndpointItem> endpointItems = new ArrayList<>();
        rdfEndpoints.forEach(e -> endpointItems.addAll(getEndpointItems(e)));
        return endpointItems;
    }

    private List<RdfEndpointItem> getEndpointItems(RdfEndpoint e) {
        try {
            String result = Request.Get(e.getEndpointUrl())
                    .addHeader(new BasicHeader("Accept", MediaType.APPLICATION_JSON))
                    .execute()
                    .returnContent()
                    .asString();

            return new Gson().fromJson(result, new TypeToken<List<RdfEndpointItem>>(){}.getType());
        } catch (IOException e1) {
            return new ArrayList<>();
        }
    }
}
