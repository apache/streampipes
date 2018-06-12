package org.streampipes.connect.rest;

import com.jayway.restassured.RestAssured;
import org.eclipse.jetty.server.Server;
import org.eclipse.rdf4j.model.Graph;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.streampipes.commons.Utils;
import org.streampipes.connect.init.Config;
import org.streampipes.connect.management.AdapterManagement;
import org.streampipes.connect.management.IAdapterManagement;
import org.streampipes.model.modelconnect.AdapterDescription;
import org.streampipes.model.modelconnect.AdapterSetDescription;
import org.streampipes.model.modelconnect.AdapterStreamDescription;

import javax.ws.rs.core.UriBuilder;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;


public class AdapterResourceTest {

    private static final String API_VERSION = "api/v1";
    private static final String ERROR_MESSAGE = "error";

    private AdapterResource adapterResource;

    private Server server;

    @Before
    public  void before() {
        Config.PORT = 8019;
        RestAssured.port = 8019;

        adapterResource = new AdapterResource();

        ResourceConfig config = new ResourceConfig().register(adapterResource);

        URI baseUri = UriBuilder
                .fromUri(Config.getBaseUrl())
                .build();

        server = JettyHttpContainerFactory.createServer(baseUri, config);
    }

    @After
    public void after() {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getAdapterSetDescription() {
        AdapterSetDescription asd = AdapterResource.getAdapterDescription(getMinimalSetAdapterJsonLD(), AdapterSetDescription.class);

        assertEquals("http://test.de/1", asd.getUri());
    }

    @Test
    public void invokeStreamAdapterSuccess() {

        // Mock adatper management
        IAdapterManagement adapterManagement = mock(AdapterManagement.class);
        org.mockito.Mockito.when(adapterManagement.invokeStreamAdapter(any(AdapterStreamDescription.class))).thenReturn("");
        adapterResource.setAdapterManagement(adapterManagement);

        // perform test
        String data = getMinimalStreamAdapterJsonLD();
        given().contentType("application/json").body(data).when()
                .post(API_VERSION + "/invoke/stream").then().assertThat()
                .body("success", equalTo(true))
                .body("optionalMessage", equalTo(""))
                .body("elementId", equalTo("http://test.de/1"));
    }

    @Test
    public void invokeStreamAdapterFail() {

        IAdapterManagement adapterManagement = mock(AdapterManagement.class);
        org.mockito.Mockito.when(adapterManagement.invokeStreamAdapter(any(AdapterStreamDescription.class)))
                .thenReturn(ERROR_MESSAGE);
        adapterResource.setAdapterManagement(adapterManagement);

        String data = getMinimalStreamAdapterJsonLD();
        given().contentType("application/json").body(data).when()
                .post(API_VERSION + "/invoke/stream").then().assertThat()
                .body("success", equalTo(false))
                .body("optionalMessage", equalTo(ERROR_MESSAGE))
                .body("elementId", equalTo("http://test.de/1"));
    }

    @Test
    public void stopStreamAdapterSuccess() {

        // Mock adatper management
        IAdapterManagement adapterManagement = mock(AdapterManagement.class);
        org.mockito.Mockito.when(adapterManagement.stopStreamAdapter(any(AdapterStreamDescription.class))).thenReturn("");
        adapterResource.setAdapterManagement(adapterManagement);

        // perform test
        String data = getMinimalStreamAdapterJsonLD();
        given().contentType("application/json").body(data).when()
                .delete(API_VERSION + "/stop/stream").then().assertThat()
                .body("success", equalTo(true))
                .body("optionalMessage", equalTo(""))
                .body("elementId", equalTo("http://test.de/1"));
    }

    @Test
    public void stopStreamAdapterFail() {

        IAdapterManagement adapterManagement = mock(AdapterManagement.class);
        org.mockito.Mockito.when(adapterManagement.stopStreamAdapter(any(AdapterStreamDescription.class)))
                .thenReturn(ERROR_MESSAGE);
        adapterResource.setAdapterManagement(adapterManagement);

        String data = getMinimalStreamAdapterJsonLD();
        given().contentType("application/json").body(data).when()
                .delete(API_VERSION + "/stop/stream").then().assertThat()
                .body("success", equalTo(false))
                .body("optionalMessage", equalTo(ERROR_MESSAGE))
                .body("elementId", equalTo("http://test.de/1"));
    }


    @Test
    public void invokeSetAdapterSuccess() {

        // Mock adatper management
        IAdapterManagement adapterManagement = mock(AdapterManagement.class);
        org.mockito.Mockito.when(adapterManagement.invokeSetAdapter(any(AdapterSetDescription.class))).thenReturn("");
        adapterResource.setAdapterManagement(adapterManagement);

        // perform test
        String data = getMinimalSetAdapterJsonLD();
        given().contentType("application/json").body(data).when()
                .post(API_VERSION + "/invoke/set").then().assertThat()
                .body("success", equalTo(true))
                .body("optionalMessage", equalTo(""))
                .body("elementId", equalTo("http://test.de/1"));
    }

    @Test
    public void invokeSetAdapterFail() {

        IAdapterManagement adapterManagement = mock(AdapterManagement.class);
        org.mockito.Mockito.when(adapterManagement.invokeSetAdapter(any(AdapterSetDescription.class)))
                .thenReturn(ERROR_MESSAGE);
        adapterResource.setAdapterManagement(adapterManagement);

        String data = getMinimalSetAdapterJsonLD();
        given().contentType("application/json").body(data).when()
                .post(API_VERSION + "/invoke/set").then().assertThat()
                .body("success", equalTo(false))
                .body("optionalMessage", equalTo(ERROR_MESSAGE))
                .body("elementId", equalTo("http://test.de/1"));
    }

    @Test
    public void stopSetAdapterSuccess() {

        // Mock adatper management
        IAdapterManagement adapterManagement = mock(AdapterManagement.class);
        org.mockito.Mockito.when(adapterManagement.stopSetAdapter(any(AdapterSetDescription.class))).thenReturn("");
        adapterResource.setAdapterManagement(adapterManagement);

        // perform test
        String data = getMinimalSetAdapterJsonLD();
        given().contentType("application/json").body(data).when()
                .delete(API_VERSION + "/stop/set").then().assertThat()
                .body("success", equalTo(true))
                .body("optionalMessage", equalTo(""))
                .body("elementId", equalTo("http://test.de/1"));
    }

    @Test
    public void stopSetAdapterFail() {

        IAdapterManagement adapterManagement = mock(AdapterManagement.class);
        org.mockito.Mockito.when(adapterManagement.stopSetAdapter(any(AdapterSetDescription.class)))
                .thenReturn(ERROR_MESSAGE);
        adapterResource.setAdapterManagement(adapterManagement);

        String data = getMinimalSetAdapterJsonLD();
        given().contentType("application/json").body(data).when()
                .delete(API_VERSION + "/stop/set").then().assertThat()
                .body("success", equalTo(false))
                .body("optionalMessage", equalTo(ERROR_MESSAGE))
                .body("elementId", equalTo("http://test.de/1"));
    }


    private String getMinimalStreamAdapterJsonLD() {
        return getMinimalAdapterJsonLD("sp:AdapterStreamDescription");
    }

    private String getMinimalSetAdapterJsonLD() {
       return getMinimalAdapterJsonLD("sp:AdapterSetDescription");
    }

    private String getMinimalAdapterJsonLD(String type) {
        return "{\n" +
                "  \"@graph\" : [ {\n" +
                "    \"@id\" : \"http://test.de/1\",\n" +
                "    \"@type\" : \""+ type + "\",\n" +
                "    \"http://www.w3.org/2000/01/rdf-schema#label\" : \"TestAdapterDescription\",\n" +
                "    \"sp:hasDataSet\" : {\n" +
                "      \"@id\" : \"urn:fzi.de:eventstream:lDVmMJ\"\n" +
                "    },\n" +
                "    \"sp:hasUri\" : \"http://test.de/1\"\n" +
                "  }, {\n" +
                "    \"@id\" : \"urn:fzi.de:eventstream:lDVmMJ\",\n" +
                "    \"@type\" : \"sp:DataSet\",\n" +
                "    \"sp:hasUri\" : \"urn:fzi.de:eventstream:lDVmMJ\"\n" +
                "  } ],\n" +
                "  \"@context\" : {\n" +
                "    \"sp\" : \"https://streampipes.org/vocabulary/v1/\",\n" +
                "    \"ssn\" : \"http://purl.oclc.org/NET/ssnx/ssn#\",\n" +
                "    \"xsd\" : \"http://www.w3.org/2001/XMLSchema#\",\n" +
                "    \"empire\" : \"urn:clarkparsia.com:empire:\",\n" +
                "    \"spi\" : \"urn:streampipes.org:spi:\"\n" +
                "  }\n" +
                "}";
    }
}