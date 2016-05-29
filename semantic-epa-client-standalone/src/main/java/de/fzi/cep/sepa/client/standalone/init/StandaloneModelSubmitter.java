package de.fzi.cep.sepa.client.standalone.init;


import de.fzi.cep.sepa.client.api.*;
import de.fzi.cep.sepa.client.init.DeclarersSingleton;
import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import org.eclipse.jetty.server.Server;
import de.fzi.cep.sepa.client.init.ModelSubmitter;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public abstract class StandaloneModelSubmitter extends ModelSubmitter {


    public void init() {
        URI baseUri = UriBuilder
                .fromUri(DeclarersSingleton.getInstance().getBaseUri())
                .build();

        ResourceConfig config = new ResourceConfig(getApiClasses());

        Server server = JettyHttpContainerFactory.createServer(baseUri, config);
    }

    private Set<Class<?>> getApiClasses() {
        Set<Class<? extends Object>> allClasses = new HashSet<>();

        allClasses.add(Element.class);
        allClasses.add(InvocableElement.class);
        allClasses.add(SecElement.class);
        allClasses.add(SepaElement.class);
        allClasses.add(SepElement.class);
        allClasses.add(WelcomePage.class);

        return  allClasses;
    }

}
