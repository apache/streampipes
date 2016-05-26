package de.fzi.cep.sepa.client.container.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.fzi.cep.sepa.client.declarer.Declarer;
import de.fzi.cep.sepa.client.declarer.InvocableDeclarer;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;

public class ElementTest {;
    @Test
    public void getByIdTest() {
        String id = "sepapathName1";
        List<Declarer> declarers = Arrays.asList(getDeclarerImpl(id), getDeclarerImpl("sepapathName2"));
        TestElementImpl elem = new TestElementImpl();
        elem.setDeclarers(declarers);

        NamedSEPAElement namedSEPAElement = elem.getById(id);

        assertEquals("sepaname", namedSEPAElement.getName());
        assertEquals("sepadescription", namedSEPAElement.getDescription());
    }

    @Test
    public void getByIdIsNullTest() {
        TestElementImpl elem = new TestElementImpl();
        elem.setDeclarers(new ArrayList<Declarer>());

        NamedSEPAElement actual = elem.getById("");
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

    private class TestElementImpl extends Element<Declarer> {
        private List<Declarer> declarers = new ArrayList<>();

        public List<Declarer> getDeclarers() {
            return declarers;
        }

        public void setDeclarers(List<Declarer> declarers) {
            this.declarers = declarers;
        }

        @Override
        protected List<Declarer> getElementDeclarers() {
            return declarers;
        }
    }
}
