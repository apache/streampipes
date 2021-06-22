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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EvaluationLogger {

    private final File file;
    private String buffer = "";
    private static EvaluationLogger instance = null;
    private final MQTT mqtt;
    private final BlockingConnection connection;

    public static EvaluationLogger getInstance(){
        if(instance==null) instance = new EvaluationLogger();
        return instance;
    }

    private EvaluationLogger(){
        String filepath = System.getenv("SP_LOGGING_FILE_PATH");
        String logging_host = System.getenv("SP_LOGGING_MQTT_HOST");
        int logging_port = Integer.parseInt(System.getenv("SP_LOGGING_MQTT_PORT"));
        if(filepath==null) throw new RuntimeException("No Logging Location provided.");
        if(!filepath.endsWith("/")) filepath = filepath + "/";
        String timePrefix = new SimpleDateFormat("yyyy-MM-dd-HHmmss").format(new Date());
        String FILE_NAME = "evaluation-logs.csv";
        String fileWithPath = new StringBuilder()
                .append(filepath)
                .append(timePrefix)
                .append("_")
                .append(FILE_NAME)
                .toString();
        this.file = new File(fileWithPath);
        try {
            if(!file.exists()){
                if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        mqtt = new MQTT();
        try {
            mqtt.setHost(logging_host, logging_port);
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

    @Deprecated
    public void addLine(Object[] elements){
        //TODO: Remove
        for(Object element:elements)
            buffer += element + ",";
        buffer += "\n";
    }

    @Deprecated
    public void writeOut(){
        //TODO: Remove
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true))){
            bufferedWriter.append(buffer);
            bufferedWriter.flush();
            buffer = "";
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logMQTT(String topic, Object[] elements){
        String message = "";
        for(Object element:elements)
            message += element + ",";
        try {
            connection.publish(topic, message.getBytes(StandardCharsets.UTF_8), QoS.AT_LEAST_ONCE, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
