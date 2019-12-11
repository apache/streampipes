/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.sinks.notifications.jvm.slack;

import com.ullink.slack.simpleslackapi.SlackChannel;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.context.EventSinkRuntimeContext;
import org.apache.streampipes.wrapper.runtime.EventSink;

import java.io.IOException;


public class SlackNotification implements EventSink<SlackNotificationParameters> {
    private SlackNotificationParameters params;

    @Override
    public void onInvocation(SlackNotificationParameters parameters, EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {
        this.params = parameters;
    }

    @Override
    public void onEvent(Event inputEvent) {

        if (params.isSendToUser()) {
            params.getSession().sendMessageToUser(params.getUserChannel(), params.getMessage(), null);
        } else {
            SlackChannel channel = params.getSession().findChannelByName(params.getUserChannel());
            params.getSession().sendMessage(channel, params.getMessage());
        }
    }

    @Override
    public void onDetach() throws SpRuntimeException {
        try {
            params.getSession().disconnect();
        } catch (IOException e) {
            throw new SpRuntimeException("Could not disconnect");
        }
    }
}
