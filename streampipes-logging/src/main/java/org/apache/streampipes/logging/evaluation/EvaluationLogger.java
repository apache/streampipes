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
package org.apache.streampipes.logging.evaluation;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class EvaluationLogger {
    private static EvaluationLogger instance = null;
    private final MQTT mqtt;
    private final BlockingConnection connection;

    public static EvaluationLogger getInstance(){
        if(instance==null) instance = new EvaluationLogger();
        return instance;
    }

    private EvaluationLogger(){
        String loggingUrl = System.getenv("SP_LOGGING_MQTT_URL");
        mqtt = new MQTT();
        try {
            mqtt.setHost(loggingUrl);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        connection = mqtt.blockingConnection();
        try {
            connection.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logMQTT(String topic, Object[] elements){
        String message = "";
        for(Object element:elements)
            message += element + ",";
        if (message.length() > 0) {
            message = message.substring(0, message.length()-1);
            try {
                connection.publish(topic, message.getBytes(StandardCharsets.UTF_8), QoS.AT_LEAST_ONCE, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
