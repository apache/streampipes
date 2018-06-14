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
import org.streampipes.vocabulary.StreamPipes;

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


        AdapterDescription a = null;

        if (ads.contains("AdapterSetDescription")){
            JsonLdTransformer jsonLdTransformer = new JsonLdTransformer(StreamPipes.ADAPTER_SET_DESCRIPTION);
            a = getDescription(jsonLdTransformer, ads, AdapterSetDescription.class);
        } else {
            JsonLdTransformer jsonLdTransformer = new JsonLdTransformer(StreamPipes.ADAPTER_STREAM_DESCRIPTION);
            a = getDescription(jsonLdTransformer, ads, AdapterStreamDescription.class);
        }

        logger.info("Add Adapter Description " + a.getUri());

        return a;
    }

    public static <T> T getDescription(JsonLdTransformer jsonLdTransformer, String s, Class<T> theClass) {

        T a = null;

        try {
            a = jsonLdTransformer.fromJsonLd(s, theClass);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return a;
    }


    public static String startStreamAdapter(AdapterStreamDescription asd, String baseUrl) {
        String url = baseUrl + "api/v1/invoke/stream";

        return postStartAdapter(url, asd);
    }

    public  String invokeAdapter(String streamId, SpDataSet dataSet, String baseUrl, AdapterStorageImpl adapterStorage) {
        String url = baseUrl + "api/v1/invoke/set";
//        String url = "http://localhost:8099/invoke/set";

        AdapterSetDescription adapterDescription = (AdapterSetDescription) adapterStorage.getAdapter(streamId);
        adapterDescription.setDataSet(dataSet);

        return postStartAdapter(url, adapterDescription);
    }

    private static String postStartAdapter(String url, AdapterDescription ad) {
        try {

//            TODO just for testing
//            url = "http://localhost:8099/api/v1/invoke/stream";

            logger.info("Trying to start adpater on endpoint: " + url);

            // TODO quick fix because otherwise it is not serialized to json-ld
            ad.setUri("http://test.adapter");
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
        JsonLdUtils.toJsonLD(object);
        String s = JsonLdUtils.toJsonLD(object);

        if (s == null) {
            logger.error("Could not serialize Object " + object + " into json ld");
        }

        return s;
    }


}
