package org.streampipes.rest.impl.connect;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.empire.core.empire.annotation.InvalidRdfException;
import org.streampipes.model.modelconnect.AdapterDescription;
import org.streampipes.model.modelconnect.AdapterSetDescription;
import org.streampipes.model.modelconnect.AdapterStreamDescription;
import org.streampipes.serializers.jsonld.JsonLdTransformer;
import org.streampipes.storage.couchdb.impl.AdapterStorageImpl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class SpConnect {

    private static final Logger logger = LoggerFactory.getLogger(SpConnectResource.class);

    public String addAdapter(AdapterDescription ad, String baseUrl) {

        new AdapterStorageImpl().storeAdapter(ad);

        if (ad instanceof AdapterStreamDescription) {
            return SpConnect.startStreamAdapter((AdapterStreamDescription) ad, baseUrl);
        }

        return SpConnectUtils.SUCCESS;
    }

    public static AdapterDescription getAdapterDescription(String ads) {

        JsonLdTransformer jsonLdTransformer = new JsonLdTransformer();

        AdapterDescription a = null;

        if (ads.contains("AdapterSetDescription")){
            a = getAdapterDescription(ads, AdapterSetDescription.class);
        } else {
            a = getAdapterDescription(ads, AdapterStreamDescription.class);
        }

        logger.info("Add Adapter Description " + a.getId());

        return a;
    }

    public static <T extends AdapterDescription> T getAdapterDescription(String ads, Class<T> theClass) {
        JsonLdTransformer jsonLdTransformer = new JsonLdTransformer();

        T a = null;

        try {
            a = jsonLdTransformer.fromJsonLd(ads, theClass);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return a;
    }


    public static String startStreamAdapter(AdapterStreamDescription asd, String baseUrl) {
        String url = baseUrl + "/invoke/stream";
        org.streampipes.model.Response response = new org.streampipes.model.Response("", false, "Adpater could not be reached");

        try {

            logger.info("Trying to start adpater on endpoint: " + url);

            String responseString = Request.Post(url)
                    .bodyString(toJsonLd(asd), ContentType.APPLICATION_JSON)
                    .connectTimeout(1000)
                    .socketTimeout(100000)
                    .execute().returnContent().asString();

            logger.info("Adapter started on endpoint: " + url + " with Response: " + responseString);

        } catch (IOException e) {
            e.printStackTrace();

            return "Adapter was not started successfully";
        }

        return SpConnectUtils.SUCCESS;
    }

    private static <T> String toJsonLd(T object) {
            JsonLdTransformer jsonLdTransformer = new JsonLdTransformer();
            String s = null;
            try {
                s = jsonLdTransformer.toJsonLd(object).toString();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InvalidRdfException e) {
                e.printStackTrace();
            }

            if (s == null) {
                logger.error("Could not serialize Object " + object + " into json ld");
            }

            return s;
   }


}
