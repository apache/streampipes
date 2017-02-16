package de.fzi.cep.sepa.manager.matching;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import de.fzi.cep.sepa.client.declarer.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.commons.exceptions.NoMatchingJsonSchemaException;
import de.fzi.cep.sepa.commons.exceptions.NoSepaInPipelineException;
import de.fzi.cep.sepa.commons.exceptions.RemoteServerNotAccessibleException;
import de.fzi.cep.sepa.manager.matching.v2.TestUtils;
import de.fzi.cep.sepa.model.client.pipeline.Pipeline;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.staticproperty.RemoteOneOfStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.sources.samples.random.RandomDataProducer;
import de.fzi.cep.sepa.sources.samples.random.RandomNumberStreamJson;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;

public class PipelineVerificationHandlerTest {

    private static final int WIREMOCK_PORT = 18089;
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WIREMOCK_PORT);

    @Test
    public void updateRemoteOneOfStaticProperty() throws NoSepaInPipelineException, NoMatchingJsonSchemaException, RemoteServerNotAccessibleException {


        stubFor(get(urlEqualTo("/invoke"))
                .willReturn(aResponse().withStatus(200).withBody("[{property_id: 'uniqueid', property_name: 'name1', property_description: 'This is a description'}]")));


        TestSepa testSepa = new TestSepa();
        Pipeline pipeline = TestUtils.makePipeline(new RandomDataProducer(), new RandomNumberStreamJson(), testSepa);

        PipelineVerificationHandler pvh = new PipelineVerificationHandler(pipeline);

        RemoteOneOfStaticProperty property = (RemoteOneOfStaticProperty) testSepa.declareModel().getStaticProperties().get(0);
        //TODO fix test
//        pvh.computeMappingProperties();
//
//
//        assertEquals(((RemoteOneOfStaticProperty) pvh.rdfRootElement.getStaticProperties().get(0)).getOptions().get(0).getName(), "uniqueid");
    }


    /**
     * Test class with the properties needed for the test
     */
    private class TestSepa implements SemanticEventProcessingAgentDeclarer {

        @Override
        public SepaDescription declareModel() {
            SepaDescription desc = new SepaDescription("TestSepa", "",
                    "");

            List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
//            staticProperties.add(StaticProperties.integerFreeTextProperty("freeText", "", ""));
            staticProperties.add(new RemoteOneOfStaticProperty("remoteOneOfStatic", "", "", "http://localhost:18089/invoke", "property_id", "property_name", "property_description", true));
            desc.setStaticProperties(staticProperties);

            return desc;
        }

        @Override
        public Response invokeRuntime(SepaInvocation invocationGraph) {
            return null;
        }

        @Override
        public Response detachRuntime(String pipelineId) {
            return null;
        }


    }
}