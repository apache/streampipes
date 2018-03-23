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

package org.streampipes.rest.notifications;

import com.google.gson.Gson;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.config.backend.BackendConfig;
import org.streampipes.messaging.InternalEventProcessor;
import org.streampipes.messaging.jms.ActiveMQConsumer;
import org.streampipes.model.Notification;
import org.streampipes.model.grounding.JmsTransportProtocol;
import org.streampipes.model.grounding.SimpleTopicDefinition;
import org.streampipes.storage.management.StorageDispatcher;

import java.text.SimpleDateFormat;

public abstract class AbstractNotificationSubscriber implements InternalEventProcessor<byte[]>, Runnable {

    protected String topic;
    protected Gson gson;

    public AbstractNotificationSubscriber(String topic) {
        this.topic = topic;
        this.gson = new Gson();
    }

    public void subscribe() throws SpRuntimeException {
        ActiveMQConsumer consumer = new ActiveMQConsumer();
        consumer.connect(getConsumerSettings(), this);
    }

    private JmsTransportProtocol getConsumerSettings() {
        JmsTransportProtocol protocol = new JmsTransportProtocol();
        protocol.setPort(BackendConfig.INSTANCE.getJmsPort());
        protocol.setBrokerHostname("tcp://" +BackendConfig.INSTANCE.getJmsHost());
        protocol.setTopicDefinition(new SimpleTopicDefinition(topic));

        return protocol;
    }

    @Override
    public void run() {
        try {
            subscribe();
        } catch (SpRuntimeException e) {
            e.printStackTrace();
        }
    }

    protected void storeNotification(Notification message) {
        StorageDispatcher.INSTANCE.getNoSqlStore()
                .getNotificationStorageApi()
                .addNotification(message);
    }

    protected String parseDate(long timestamp) {
        return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(timestamp);
    }

}
