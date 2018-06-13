package org.streampipes.rest.impl.connect;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;
import org.streampipes.model.Response;
import org.streampipes.model.modelconnect.AdapterDescription;
import org.streampipes.model.modelconnect.AdapterSetDescription;
import org.streampipes.model.modelconnect.AdapterStreamDescription;
import org.streampipes.rest.Mock;
import org.streampipes.rest.TestUtil;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;

public class SpConnectTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(Mock.PORT);

    @Test
    public void getAdapterSetDescriptionWithType() {
        AdapterSetDescription asd = SpConnect
                .getAdapterDescription(TestUtil.getMinimalSetAdapterJsonLD(), AdapterSetDescription.class);

        assertEquals("http://test.de/1", asd.getUri());
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

        String result = SpConnect.startStreamAdapter(adapter, Mock.HOST);

        assertEquals(SpConnectUtils.SUCCESS, result);
        verify(postRequestedFor(urlEqualTo("/invoke/stream"))
                .withHeader("Content-Type", equalTo("application/json; charset=UTF-8")));

    }


}