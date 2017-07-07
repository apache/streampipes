package de.fzi.cep.sepa.rest.impl;

import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.rest.api.IApplicationLink;

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

    private List<de.fzi.cep.sepa.model.impl.ApplicationLink> generateAppLinks() {
        List<NamedSEPAElement> allElements = new ArrayList<>();
        List<de.fzi.cep.sepa.model.impl.ApplicationLink> allApplicationLinks = new ArrayList<>();

        allElements.addAll(getPipelineElementRdfStorage()
                .getAllSEPAs().stream().map(e -> new SepaDescription(e)).collect(Collectors.toList()));
        allElements.addAll(getPipelineElementRdfStorage()
                .getAllSECs().stream().map(e -> new SecDescription(e)).collect(Collectors.toList()));
        allElements.addAll(getPipelineElementRdfStorage()
                .getAllSEPs().stream().map(e -> new SepDescription(e)).collect(Collectors.toList()));

        allElements.stream().forEach(e -> allApplicationLinks.addAll(removeDuplicates(allApplicationLinks, e.getApplicationLinks())));

        return allApplicationLinks;
    }

    private List<de.fzi.cep.sepa.model.impl.ApplicationLink> removeDuplicates(List<de.fzi.cep.sepa.model.impl.ApplicationLink> allApplicationLinks,
                                                                              List<de.fzi.cep.sepa.model.impl.ApplicationLink> applicationLinks) {
        List<de.fzi.cep.sepa.model.impl.ApplicationLink> result = new ArrayList<>();

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
