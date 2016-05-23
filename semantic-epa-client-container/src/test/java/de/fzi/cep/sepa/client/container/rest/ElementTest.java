package de.fzi.cep.sepa.client.container.rest;

import com.clarkparsia.empire.SupportsRdfId;
import com.clarkparsia.empire.annotation.SupportsRdfIdImpl;
import de.fzi.cep.sepa.client.container.init.DeclarersSingleton;
import de.fzi.cep.sepa.desc.declarer.Declarer;
import de.fzi.cep.sepa.desc.declarer.InvocableDeclarer;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import org.junit.Test;

import javax.script.Invocable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ElementTest {

    @Test
    public void getByIdTest() {
        String id = "sepapathName1";
        List<Declarer> declarers = Arrays.asList(getDeclarerImpl(id), getDeclarerImpl("sepapathName2"));
        SepElement sep = new SepElement();

        NamedSEPAElement namedSEPAElement = sep.getById(declarers, id);

        assertEquals(id, namedSEPAElement.getUri());
        assertEquals("sepaname", namedSEPAElement.getName());
        assertEquals("sepadescription", namedSEPAElement.getDescription());
    }

    @Test
    public void getByIdIsNullTest() {
        SepElement sep = new SepElement();
        NamedSEPAElement actual = sep.getById(new ArrayList<Declarer>(), "");
        assertNull(actual);
    }

    @Test
    public void toJsonLdNullTest() {
        SepElement sep = new SepElement();
        assertEquals("{}", sep.toJsonLd(null));
    }


    private DeclarerImpl getDeclarerImpl(String id) {
        return new DeclarerImpl(id);
    }


    private class DeclarerImpl implements InvocableDeclarer<SepaDescription, SepaInvocation> {
        private String id;

        public DeclarerImpl(String id) {
            this.id = id;
        }

        @Override
        public Response invokeRuntime(SepaInvocation invocationGraph) {
            return null;
        }

        @Override
        public Response detachRuntime(String pipelineId) {
            return null;
        }

        @Override
        public SepaDescription declareModel() {
            return new SepaDescription(id, "sepaname", "sepadescription", "sepaiconUrl");
        }
    }
}