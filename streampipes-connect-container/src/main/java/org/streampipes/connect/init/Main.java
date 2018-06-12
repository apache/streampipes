package org.streampipes.connect.init;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.streampipes.connect.rest.AdapterResource;
import org.streampipes.connect.rest.WelcomePage;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class Main {

    public static void main(String... args) {
        ResourceConfig config = new ResourceConfig(getApiClasses());


        URI baseUri = UriBuilder
                .fromUri(Config.getBaseUrl())
                .build();

        Server server = JettyHttpContainerFactory.createServer(baseUri, config);

    }

    private static Set<Class<?>> getApiClasses() {
        Set<Class<? extends Object>> allClasses = new HashSet<>();

        allClasses.add(WelcomePage.class);
        allClasses.add(AdapterResource.class);

        return allClasses;
    }
}
