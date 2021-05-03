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
package org.apache.streampipes.model;

public class NodeHealthStatus {

    private String nodeControllerId;
    private Boolean success;
    private long timestamp;
    private String optionalMessage;

    public NodeHealthStatus() {
    }

    public NodeHealthStatus(Boolean success) {
        this.success = success;
    }

    public NodeHealthStatus(String nodeControllerId, Boolean success, long timestamp, String optionalMessage) {
        this.nodeControllerId = nodeControllerId;
        this.success = success;
        this.timestamp = timestamp;
        this.optionalMessage = optionalMessage;
    }

    public String getNodeControllerId() {
        return nodeControllerId;
    }

    public void setNodeControllerId(String nodeControllerId) {
        this.nodeControllerId = nodeControllerId;
    }

    public Boolean getSuccess() {
        return success;
    }

    public Boolean isSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getOptionalMessage() {
        return optionalMessage;
    }

    public void setOptionalMessage(String optionalMessage) {
        this.optionalMessage = optionalMessage;
    }
}
