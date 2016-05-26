package de.fzi.cep.sepa.client.standalone.init;


import de.fzi.cep.sepa.client.api.*;
import de.fzi.cep.sepa.client.init.DeclarersSingleton;
import org.eclipse.jetty.server.Server;
import de.fzi.cep.sepa.client.init.ModelSubmitter;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public abstract class StandaloneModelSubmitter extends ModelSubmitter {

    public void init() {
//        URI baseUri = UriBuilder.fromUri(getBaseUri()).build();
        URI baseUri = UriBuilder
                .fromUri("http://localhost/")
                .port(8080).build();
        ResourceConfig config = new ResourceConfig(Element.class, InvocableElement.class, SecElement.class,
                SepaElement.class, SepElement.class, WelcomePage.class);

        Server server = JettyHttpContainerFactory.createServer(baseUri, config);
    }

}
