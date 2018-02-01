package org.streampipes.pe.sinks.standalone.samples.onesignal;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.wrapper.runtime.EventSink;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class OneSignalProducer implements EventSink<OneSignalParameters> {

    private String content;
    private String appId;
    private String apiKey;

    public OneSignalProducer() {

    }

    @Override
    public void bind(OneSignalParameters parameters) throws SpRuntimeException {
        this.content = parameters.getContent();
        this.appId = parameters.getAppId();
        this.apiKey = parameters.getApiKey();
    }

    @Override
    public void onEvent(Map<String, Object> event, String sourceInfo) {

        String jsondata = "{\"app_id\": \"" + appId + "\",\"contents\": {\"en\": \"" + content + "\"}, \"included_segments\":[\"All\"]}";

        StringEntity jsonparam = null;
        try {
            jsonparam = new StringEntity(jsondata);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        jsonparam.setContentType("application/json;charset=utf-8");
        jsonparam.setChunked(true);

        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("https://onesignal.com/api/v1/notifications");
        httppost.addHeader("Authorization", "Basic " + this.apiKey);
        httppost.setEntity(jsonparam);

        HttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity entity = response.getEntity();

    }

    @Override
    public void discard() throws SpRuntimeException {
    }
}
