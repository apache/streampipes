/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.sinks.notifications.jvm.slack;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ullink.slack.simpleslackapi.SlackChannel;
import org.streampipes.messaging.InternalEventProcessor;

import java.io.UnsupportedEncodingException;


public class SlackNotification implements InternalEventProcessor<byte[]> {
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
