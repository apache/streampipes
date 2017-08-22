package org.streampipes.pe.slack.sec;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ullink.slack.simpleslackapi.SlackChannel;
import org.streampipes.messaging.EventListener;

import java.io.UnsupportedEncodingException;


public class SlackNotification implements EventListener<byte[]> {
    private SlackNotificationParameters params;
    public SlackNotification(SlackNotificationParameters params) {
        this.params = params;
    }

    @Override
    public void onEvent(byte[] payload) {
        String message = "";
        try {
            JsonElement element = new JsonParser().parse(new String(payload, "UTF-8"));
            JsonObject jobject = element.getAsJsonObject();

            for (String s : params.getProperties()) {
                message +=  s + " : " + jobject.get(s) + "\n";
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        if (params.isSendToUser()) {
            params.getSession().sendMessageToUser(params.getUserChannel(), message, null);
        } else {
            SlackChannel channel = params.getSession().findChannelByName(params.getUserChannel());
            params.getSession().sendMessage(channel, message);
        }
    }

    public SlackNotificationParameters getParams() {
        return params;
    }
}
