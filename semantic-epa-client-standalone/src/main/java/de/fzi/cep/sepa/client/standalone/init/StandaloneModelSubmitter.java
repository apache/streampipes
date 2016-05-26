package de.fzi.cep.sepa.client.standalone.init;


import de.fzi.cep.sepa.client.init.ModelSubmitter;

import java.net.URI;

public class StandaloneModelSubmitter extends ModelSubmitter {

    public void init() {
//        URI baseUri = UriBuilder.fromUri("http://localhost/").port(9998).build();
//        ResourceConfig config = new ResourceConfig(ContainerModelSubmitter.class);
//        Server server = JettyHttpContainerFactory.createServer(baseUri, config);
    }
}
