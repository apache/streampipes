package org.streampipes.rest.impl;

import org.streampipes.model.NamedSEPAElement;
import org.streampipes.model.impl.graph.SecDescription;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.rest.api.IApplicationLink;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by riemer on 11.10.2016.
 */

@Path("/v2/applink")
public class ApplicationLink extends AbstractRestInterface implements IApplicationLink {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getApplicationLinks() {
        return ok(generateAppLinks());
    }

    private List<org.streampipes.model.impl.ApplicationLink> generateAppLinks() {
        List<NamedSEPAElement> allElements = new ArrayList<>();
        List<org.streampipes.model.impl.ApplicationLink> allApplicationLinks = new ArrayList<>();

        allElements.addAll(getPipelineElementRdfStorage()
                .getAllSEPAs().stream().map(e -> new SepaDescription(e)).collect(Collectors.toList()));
        allElements.addAll(getPipelineElementRdfStorage()
                .getAllSECs().stream().map(e -> new SecDescription(e)).collect(Collectors.toList()));
        allElements.addAll(getPipelineElementRdfStorage()
                .getAllSEPs().stream().map(e -> new SepDescription(e)).collect(Collectors.toList()));

        allElements.stream().forEach(e -> allApplicationLinks.addAll(removeDuplicates(allApplicationLinks, e.getApplicationLinks())));

        return allApplicationLinks;
    }

    private List<org.streampipes.model.impl.ApplicationLink> removeDuplicates(List<org.streampipes.model.impl.ApplicationLink> allApplicationLinks,
                                                                              List<org.streampipes.model.impl.ApplicationLink> applicationLinks) {
        List<org.streampipes.model.impl.ApplicationLink> result = new ArrayList<>();

        applicationLinks.forEach( a -> {
                if (allApplicationLinks
                        .stream()
                        .noneMatch(existing -> existing.getApplicationUrl()
                                .equals(existing.getApplicationUrl()))) {
                    result.add(a);
                }
        });

        return result;

    }
}
