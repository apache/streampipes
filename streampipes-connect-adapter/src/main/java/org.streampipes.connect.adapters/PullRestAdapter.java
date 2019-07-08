package org.streampipes.connect.adapter.specific;

import com.google.gson.Gson;
import org.apache.http.client.fluent.Request;
import org.streampipes.connect.adapter.exception.AdapterException;
import org.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;

public abstract class PullRestAdapter extends PullAdapter {

    public PullRestAdapter() {
        super();
    }

    public PullRestAdapter(SpecificAdapterStreamDescription adapterDescription) {
        super(adapterDescription);
    }

    protected static String getDataFromEndpointString(String url) throws AdapterException {
        String result = null;


        logger.info("Started Request to open sensemap endpoint: " + url);
        try {
            result = Request.Get(url)
                    .connectTimeout(1000)
                    .socketTimeout(100000)
                    .execute().returnContent().asString();


            if (result.startsWith("ï")) {
                result = result.substring(3);
            }

            logger.info("Received data from request");

        } catch (Exception e) {
            String errorMessage = "Error while connecting to the open sensemap api";
            logger.error(errorMessage, e);
            throw new AdapterException(errorMessage);
        }

        return result;
    }

    protected static <T> T getDataFromEndpoint(String url, Class<T> clazz) throws AdapterException {

        String rawJson = getDataFromEndpointString(url);
        T all = new Gson().fromJson(rawJson, clazz);

        return all;
    }

}
