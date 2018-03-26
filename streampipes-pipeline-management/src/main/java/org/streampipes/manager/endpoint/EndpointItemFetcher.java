/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.manager.endpoint;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.model.client.endpoint.RdfEndpoint;
import org.streampipes.model.client.endpoint.RdfEndpointItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

public class EndpointItemFetcher {
    Logger logger = LoggerFactory.getLogger(EndpointItemFetcher.class);

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
            logger.warn("Processing Element Descriptions could not be fetched from RDF endpoint: " + e.getEndpointUrl(), e1);
            return new ArrayList<>();
        }
    }
}
