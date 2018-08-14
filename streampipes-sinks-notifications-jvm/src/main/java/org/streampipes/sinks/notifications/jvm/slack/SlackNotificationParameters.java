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

import com.ullink.slack.simpleslackapi.SlackSession;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;

public class SlackNotificationParameters extends EventSinkBindingParams {
    private String authToken;
    private boolean sendToUser;
    private String userChannel;
    private String message;
    private SlackSession session;

    public SlackNotificationParameters(String authToken, boolean sendToUser, String userChannel, String message, SlackSession session) {
        this.authToken = authToken;
        this.sendToUser = sendToUser;
        this.userChannel = userChannel;
        this.message = message;
        this.session = session;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public boolean isSendToUser() {
        return sendToUser;
    }

    public void setSendToUser(boolean sendToUser) {
        this.sendToUser = sendToUser;
    }

    public String getUserChannel() {
        return userChannel;
    }

    public void setUserChannel(String userChannel) {
        this.userChannel = userChannel;
    }

    public String getMessage() {
        return message;
    }

    public SlackSession getSession() {
        return session;
    }

    public void setSession(SlackSession session) {
        this.session = session;
    }
}

