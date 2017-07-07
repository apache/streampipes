package org.streampipes.container.standalone.init;


import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.streampipes.container.api.Element;
import org.streampipes.container.api.InvocableElement;
import org.streampipes.container.api.SecElement;
import org.streampipes.container.api.SepElement;
import org.streampipes.container.api.SepaElement;
import org.streampipes.container.api.WelcomePage;
import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.container.init.ModelSubmitter;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.UriBuilder;

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
