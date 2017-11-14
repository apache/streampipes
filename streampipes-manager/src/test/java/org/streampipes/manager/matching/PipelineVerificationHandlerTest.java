package org.streampipes.manager.matching;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.streampipes.container.declarer.SemanticEventProcessingAgentDeclarer;
import org.streampipes.model.Response;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.staticproperty.RemoteOneOfStaticProperty;
import org.streampipes.model.staticproperty.StaticProperty;
import org.junit.Rule;

import java.util.ArrayList;
import java.util.List;

public class PipelineVerificationHandlerTest {

    private static final int WIREMOCK_PORT = 18089;
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WIREMOCK_PORT);

    //TODO fix test
//    @Test
//    public void updateRemoteOneOfStaticProperty() throws NoSepaInPipelineException, NoMatchingJsonSchemaException, RemoteServerNotAccessibleException {
//
//
//        stubFor(get(urlEqualTo("/invoke"))
//                .willReturn(aResponse().withStatus(200).withBody("[{property_id: 'uniqueid', property_name: 'name1', property_description: 'This is a description'}]")));
//
//
//        TestSepa testSepa = new TestSepa();
//        Pipeline pipeline = TestUtils.makePipeline(new RandomDataProducer(), new RandomNumberStreamJson(), testSepa);
//
//        PipelineVerificationHandler pvh = new PipelineVerificationHandler(pipeline);
//
//        RemoteOneOfStaticProperty property = (RemoteOneOfStaticProperty) testSepa.declareModel().getStaticProperties().get(0);
//        pvh.computeMappingProperties();
//
//
//        assertEquals(((RemoteOneOfStaticProperty) pvh.rdfRootElement.getStaticProperties().get(0)).getOptions().get(0).getName(), "uniqueid");
//    }


    /**
     * Test class with the properties needed for the test
     */
    private class TestSepa implements SemanticEventProcessingAgentDeclarer {

        @Override
        public DataProcessorDescription declareModel() {
            DataProcessorDescription desc = new DataProcessorDescription("TestSepa", "",
                    "");

            List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
//            staticProperties.add(StaticProperties.integerFreeTextProperty("freeText", "", ""));
            staticProperties.add(new RemoteOneOfStaticProperty("remoteOneOfStatic", "", "", "http://localhost:18089/invoke", "property_id", "property_name", "property_description", true));
            desc.setStaticProperties(staticProperties);

            return desc;
        }

        @Override
        public Response invokeRuntime(DataProcessorInvocation invocationGraph) {
            return null;
        }

        @Override
        public Response detachRuntime(String pipelineId) {
            return null;
        }


    }
}