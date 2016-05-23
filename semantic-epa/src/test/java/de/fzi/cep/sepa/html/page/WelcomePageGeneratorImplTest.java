package de.fzi.cep.sepa.html.page;

import de.fzi.cep.sepa.desc.declarer.Declarer;
import de.fzi.cep.sepa.desc.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.html.model.Description;
import de.fzi.cep.sepa.html.model.SemanticEventProducerDescription;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;

public class WelcomePageGeneratorImplTest {

    @Test
    public void buildUrisWithEmptyListTest() {
        WelcomePageGenerator wpg = new WelcomePageGeneratorImpl("baseUri", new ArrayList<Declarer>());
        List<Description> actual = wpg.buildUris();

        assertEquals(actual.size(), 0);
    }

    @Test
    public void buildUrisWithSepaTest() throws URISyntaxException {
        WelcomePageGenerator wpg = new WelcomePageGeneratorImpl("baseUri/", Arrays.asList(getSepaDeclarer()));
        List<Description> actual = wpg.buildUris();
        Description expected = new Description("sepaname", "sepadescription", new URI("baseUri/sepapathName"));

        assertEquals(1, actual.size());
        assertEquals(expected, actual.get(0));
    }

    @Test
    public void buildUrisWithSepTest() throws URISyntaxException {
        WelcomePageGenerator wpg = new WelcomePageGeneratorImpl("baseUri/", Arrays.asList(getSepdDeclarer()));
        List<Description> actual = wpg.buildUris();
        Description expected = new Description("sepname", "sepdescription", new URI("baseUri/seppathName"));

        assertEquals(actual.size(), 1);
        Description desc = actual.get(0);
        assertEquals(expected.getName(), desc.getName());
        assertEquals(expected.getDescription(), desc.getDescription());
        assertEquals(expected.getUri(), desc.getUri());

        assertThat(desc,  instanceOf(SemanticEventProducerDescription.class));

        SemanticEventProducerDescription sepDesc = (SemanticEventProducerDescription) desc;
        assertEquals(1, sepDesc.getStreams().size());
        Description expectedStream = new Description("streamname", "streamdescription", new URI("baseUri/streampathName"));

        assertEquals(expectedStream, sepDesc.getStreams().get(0));
    }

    private SemanticEventProcessingAgentDeclarer getSepaDeclarer() {
        return new SemanticEventProcessingAgentDeclarer() {
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
                return new SepaDescription("sepapathName", "sepaname", "sepadescription", "iconUrl");
            }
        };
    }

    private SemanticEventProducerDeclarer getSepdDeclarer() {
        return new SemanticEventProducerDeclarer() {
            @Override
            public List<EventStreamDeclarer> getEventStreams() {
                return Arrays.asList(new EventStreamDeclarer() {
                    @Override
                    public EventStream declareModel(SepDescription sep) {
                        return new EventStream("streampathName", "streamname", "streamdescription", null);
                    }

                    @Override
                    public void executeStream() {

                    }

                    @Override
                    public boolean isExecutable() {
                        return false;
                    }
                });
            }

            @Override
            public SepDescription declareModel() {
                return new SepDescription("seppathName", "sepname", "sepdescription", "sepiconUrl");
            }
        };
    }

}