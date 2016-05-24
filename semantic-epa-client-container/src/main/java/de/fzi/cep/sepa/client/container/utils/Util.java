package de.fzi.cep.sepa.client.container.utils;

import com.google.gson.Gson;
import de.fzi.cep.sepa.client.container.init.EmbeddedModelSubmitter;
import de.fzi.cep.sepa.model.impl.Response;

public class Util {
    public static String getInstanceId(String url, String type, String elemntId) {
        return url.replace(EmbeddedModelSubmitter.getBaseUri() + type + "/" + elemntId + "/", "");
    }

    public static String toResponseString(String elementId, boolean success) {
        return toResponseString(elementId, success, "");
    }

    public static String toResponseString(String elementId, boolean success, String optionalMessage) {
        Gson gson = new Gson();
        return gson.toJson(new Response(elementId, success, optionalMessage));
    }

    public static String toResponseString(Response response) {
        Gson gson = new Gson();
        return gson.toJson(response);
    }
}
