package org.streampipes.rest.impl.connect;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.empire.core.empire.annotation.InvalidRdfException;
import org.streampipes.model.SpDataSet;
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
        return getDescription(ads, theClass);
    }

    public static SpDataSet getDataSetDescritpion(String s) {
        return getDescription(s, SpDataSet.class);
    }

    private static <T> T getDescription(String s, Class<T> theClass) {
        JsonLdTransformer jsonLdTransformer = new JsonLdTransformer();

        T a = null;

        try {
            a = jsonLdTransformer.fromJsonLd(s, theClass);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return a;
    }


    public static String startStreamAdapter(AdapterStreamDescription asd, String baseUrl) {
        String url = baseUrl + "/invoke/stream";

        return postStartAdapter(url, asd);
    }

    public  String invokeAdapter(String streamId, SpDataSet dataSet, String baseUrl, AdapterStorageImpl adapterStorage) {
        String url = baseUrl + "/invoke/set";

        AdapterSetDescription adapterDescription = (AdapterSetDescription) adapterStorage.getAdapter(streamId);
        adapterDescription.setDataSet(dataSet);

        return postStartAdapter(url, adapterDescription);
    }

    private static String postStartAdapter(String url, AdapterDescription ad) {
        try {

            logger.info("Trying to start adpater on endpoint: " + url);

            String adapterDescription = toJsonLd(ad);

            String responseString = Request.Post(url)
                    .bodyString(adapterDescription, ContentType.APPLICATION_JSON)
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
