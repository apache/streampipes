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
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.messaging.InternalEventProcessor;
import org.streampipes.sinks.notifications.jvm.onesignal.OneSignalParameters;
import org.streampipes.wrapper.runtime.EventSink;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;


public class SlackNotification extends EventSink<SlackNotificationParameters> {
    private SlackNotificationParameters params;

    public SlackNotification(SlackNotificationParameters params) {
        super(params);
    }

    public SlackNotificationParameters getParams() {
        return params;
    }

    @Override
    public void bind(SlackNotificationParameters parameters) throws SpRuntimeException {
        this.params = parameters;
    }

    @Override
    public void onEvent(Map<String, Object> event, String sourceInfo) {

        if (params.isSendToUser()) {
            params.getSession().sendMessageToUser(params.getUserChannel(), params.getMessage(), null);
        } else {
            SlackChannel channel = params.getSession().findChannelByName(params.getUserChannel());
            params.getSession().sendMessage(channel, params.getMessage());
        }
    }

    @Override
    public void discard() throws SpRuntimeException {
        try {
            params.getSession().disconnect();
        } catch (IOException e) {
            throw new SpRuntimeException("Could not disconnect");
        }
    }
}
