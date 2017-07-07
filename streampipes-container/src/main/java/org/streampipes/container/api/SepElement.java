package org.streampipes.container.api;

import java.util.List;

import javax.ws.rs.Path;

import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.container.init.DeclarersSingleton;

@Path("/sep")
public class SepElement extends Element<SemanticEventProducerDeclarer> {

    @Override
    protected List<SemanticEventProducerDeclarer> getElementDeclarers() {
        return DeclarersSingleton.getInstance().getProducerDeclarers();
    }
}
