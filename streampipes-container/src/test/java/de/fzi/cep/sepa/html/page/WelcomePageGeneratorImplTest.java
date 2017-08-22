package de.fzi.cep.sepa.html.page;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import org.streampipes.container.declarer.Declarer;
import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProcessingAgentDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.container.html.model.Description;
import org.streampipes.container.html.model.SemanticEventProducerDescription;
import org.streampipes.container.html.page.WelcomePageGenerator;
import org.streampipes.container.html.page.WelcomePageGeneratorImpl;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;

public class WelcomePageGeneratorImplTest {

    @Test
    public void buildUrisWithEmptyListTest() {
        WelcomePageGenerator wpg = new WelcomePageGeneratorImpl("baseUri", new ArrayList<Declarer>());
        List<Description> actual = wpg.buildUris();

        assertEquals(actual.size(), 0);
    }

    @Test
    public void buildUrisWithSepaTest() {
        WelcomePageGenerator wpg = new WelcomePageGeneratorImpl("baseUri/", Arrays.asList(getSepaDeclarer()));
        List<Description> actual = wpg.buildUris();
        Description expected = new Description("sepaname", "sepadescription", URI.create("baseUri/sepa/sepapathName"));

        assertEquals(1, actual.size());
        assertEquals(expected, actual.get(0));
    }

    @Test
    public void buildUrisWithSepTest() {
        WelcomePageGenerator wpg = new WelcomePageGeneratorImpl("baseUri/", Arrays.asList(getSepdDeclarer()));
        List<Description> actual = wpg.buildUris();
        Description expected = new Description("sepname", "sepdescription", URI.create("baseUri/sep/seppathName"));

        assertEquals(actual.size(), 1);
        Description desc = actual.get(0);
        assertEquals(expected.getName(), desc.getName());
        assertEquals(expected.getDescription(), desc.getDescription());
        assertEquals(expected.getUri(), desc.getUri());

        assertThat(desc,  instanceOf(SemanticEventProducerDescription.class));

        SemanticEventProducerDescription sepDesc = (SemanticEventProducerDescription) desc;
        assertEquals(1, sepDesc.getStreams().size());
        Description expectedStream = new Description("streamname", "streamdescription", URI.create("baseUri/stream/streampathName"));

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