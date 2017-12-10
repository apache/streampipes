package org.streampipes.container.util;

import com.google.gson.Gson;
import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.model.Response;

public class Util {
    public static String getInstanceId(String url, String type, String elemntId) {
        return url.replace(DeclarersSingleton.getInstance().getBaseUri() + type + "/" + elemntId + "/", "");
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
