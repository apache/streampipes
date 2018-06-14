package org.streampipes.rest.impl.connect;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.streampipes.model.Response;
import org.streampipes.model.SpDataSet;
import org.streampipes.model.modelconnect.AdapterDescription;
import org.streampipes.model.modelconnect.AdapterSetDescription;
import org.streampipes.model.modelconnect.AdapterStreamDescription;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.rest.Mock;
import org.streampipes.rest.TestUtil;
import org.streampipes.storage.couchdb.impl.AdapterStorageImpl;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class SpConnectTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(Mock.PORT);

    @Before
    public  void before() {
    }

    @Test
    public void getAdapterStreamDescriptionWithoutType() {
        AdapterDescription asd = SpConnect
                .getAdapterDescription(TestUtil.getMinimalStreamAdapterJsonLD());

        assertTrue(asd instanceof AdapterStreamDescription);
    }

    @Test
    public void getAdapterSetDescriptionWithoutType() {
        AdapterDescription asd = SpConnect.
                getAdapterDescription(TestUtil.getMinimalSetAdapterJsonLD());

        assertTrue(asd instanceof AdapterSetDescription);
    }

    @Test
    public void startStreamAdapterTest() {
       // expected http request to connect-container /invoke/stream
        Response expected = new Response("id",true);
        stubFor(post(urlEqualTo("/invoke/stream"))
                .willReturn(aResponse()
                .withStatus(200)
                .withBody(expected.toString())));

        AdapterStreamDescription adapter = new AdapterStreamDescription();
        adapter.setUri("http://test.de/1");

        String result = SpConnect.startStreamAdapter(adapter, Mock.HOST + "/");

        assertEquals(SpConnectUtils.SUCCESS, result);
        verify(postRequestedFor(urlEqualTo("/invoke/stream"))
                .withHeader("Content-Type", equalTo("application/json; charset=UTF-8")));

    }

    @Test
    public void invokeAdapterTest() {
        Response expected = new Response("id",true);
        stubFor(post(urlEqualTo("/invoke/set"))
                .willReturn(aResponse()
                .withStatus(200)
                .withBody(expected.toString())));

        AdapterSetDescription adapterSetDescription = new AdapterSetDescription();
        adapterSetDescription.setUri("http://test.adapter");
        AdapterStorageImpl adapterStorage = mock(AdapterStorageImpl.class);
        org.mockito.Mockito.when(adapterStorage.getAdapter(org.mockito.ArgumentMatchers.any(String.class)))
                .thenReturn(adapterSetDescription);


        SpDataSet dataSet = new SpDataSet("http://one.de", "name", "desc", new EventSchema());

        SpConnect spConnect = new SpConnect();
        String result = spConnect.invokeAdapter("1234", dataSet, Mock.HOST + "/", adapterStorage);

        assertEquals(SpConnectUtils.SUCCESS, result);
        verify(postRequestedFor(urlEqualTo("/invoke/set"))
                .withHeader("Content-Type", equalTo("application/json; charset=UTF-8")));

    }


}