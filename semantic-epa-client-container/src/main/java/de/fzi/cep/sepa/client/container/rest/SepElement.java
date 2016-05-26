package de.fzi.cep.sepa.client.container.rest;

import java.util.List;

import javax.ws.rs.Path;

import de.fzi.cep.sepa.client.container.init.DeclarersSingleton;
import de.fzi.cep.sepa.client.declarer.SemanticEventProducerDeclarer;

@Path("/sep")
public class SepElement extends Element<SemanticEventProducerDeclarer> {

    @Override
    protected List<SemanticEventProducerDeclarer> getElementDeclarers() {
        return DeclarersSingleton.getInstance().getProducerDeclarers();
    }
}
