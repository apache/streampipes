package de.fzi.cep.sepa.client.container.rest;

import de.fzi.cep.sepa.client.container.init.DeclarersSingleton;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProducerDeclarer;

import javax.ws.rs.Path;
import java.util.List;

@Path("/sep")
public class SepElement extends Element<SemanticEventProducerDeclarer> {

    @Override
    protected List<SemanticEventProducerDeclarer> getElementDeclarers() {
        return DeclarersSingleton.getInstance().getProducerDeclarers();
    }
}
