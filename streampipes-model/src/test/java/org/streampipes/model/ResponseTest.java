package org.streampipes.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class ResponseTest {

    @Test
    public void toStringWithMessageTest() {
        Response resp = new Response("id", true);

        String result = resp.toString();

        assertEquals("{\"success\":true,\"elementId\":\"id\",\"optionalMessage\":\"\"}", result);
    }

    @Test
    public void toStringWithoutMessageTest() {
        Response resp = new Response("id", false, "error");

        String result = resp.toString();

        assertEquals("{\"success\":false,\"elementId\":\"id\",\"optionalMessage\":\"error\"}", result);

    }
}