package de.fzi.cep.sepa.client.api;

import java.util.List;

import javax.ws.rs.Path;

import de.fzi.cep.sepa.client.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.client.init.DeclarersSingleton;

@Path("/sep")
public class SepElement extends Element<SemanticEventProducerDeclarer> {

    @Override
    protected List<SemanticEventProducerDeclarer> getElementDeclarers() {
        return DeclarersSingleton.getInstance().getProducerDeclarers();
    }
}
