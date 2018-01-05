package org.streampipes.rest.impl;

import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.rest.api.IApplicationLink;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v2/applink")
public class ApplicationLink extends AbstractRestInterface implements IApplicationLink {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getApplicationLinks() {
        return ok(generateAppLinks());
    }

    private List<org.streampipes.model.ApplicationLink> generateAppLinks() {
        List<NamedStreamPipesEntity> allElements = new ArrayList<>();
        List<org.streampipes.model.ApplicationLink> allApplicationLinks = new ArrayList<>();

        allElements.addAll(getPipelineElementRdfStorage()
                .getAllSEPAs().stream().map(e -> new DataProcessorDescription(e)).collect(Collectors.toList()));
        allElements.addAll(getPipelineElementRdfStorage()
                .getAllSECs().stream().map(e -> new DataSinkDescription(e)).collect(Collectors.toList()));
        allElements.addAll(getPipelineElementRdfStorage()
                .getAllSEPs().stream().map(e -> new DataSourceDescription(e)).collect(Collectors.toList()));

        allElements.stream().forEach(e -> allApplicationLinks.addAll(removeDuplicates(allApplicationLinks, e.getApplicationLinks())));

        return allApplicationLinks;
    }

    private List<org.streampipes.model.ApplicationLink> removeDuplicates(List<org.streampipes.model.ApplicationLink> allApplicationLinks,
                                                                         List<org.streampipes.model.ApplicationLink> applicationLinks) {
        List<org.streampipes.model.ApplicationLink> result = new ArrayList<>();

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
