package org.apache.streampipes.node.controller.container.management.relay.model;/*
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

public class Relay {
    private final String sourceHost;
    private final int sourcePort;
    private final String targetHost;
    private final int targetPort;
    private final String topic;

    public Relay(String sourceUrl, String targetUrl, String topic) {
        this.sourceHost = sourceUrl.split(":")[0];
        this.sourcePort = Integer.parseInt(sourceUrl.split(":")[1]);
        this.targetHost = targetUrl.split(":")[0];
        this.targetPort = Integer.parseInt(targetUrl.split(":")[1]);
        this.topic = topic;
    }

    public Relay(String sourceHost, int sourcePort, String targetHost, int targetPort, String topic) {
        this.sourceHost = sourceHost;
        this.sourcePort = sourcePort;
        this.targetHost = targetHost;
        this.targetPort = targetPort;
        this.topic = topic;
    }

    public String getSourceHost() {
        return sourceHost;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public String getTargetHost() {
        return targetHost;
    }

    public int getTargetPort() {
        return targetPort;
    }

    public String getTopic() {
        return topic;
    }
}
