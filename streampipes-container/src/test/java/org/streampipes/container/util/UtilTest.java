package org.streampipes.container.util;

import org.junit.Test;
import org.streampipes.model.Response;

import static org.junit.Assert.*;

public class UtilTest {

    @Test
    public void fromResponseStringSuccessTest() {
        Response expected = new Response("id", true);
        String dataString = expected.toString();

        Response result = Util.fromResponseString(dataString);

        assertEquals(expected, result);
    }

    @Test
    public void fromResponseStringFailTest() {
        String data = "{\"bl\":1}";
        Response result = Util.fromResponseString(data);

        assertNull(result);
    }
}