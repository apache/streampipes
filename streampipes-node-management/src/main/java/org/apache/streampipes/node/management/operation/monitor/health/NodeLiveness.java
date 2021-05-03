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
package org.apache.streampipes.node.management.operation.monitor.health;

import org.apache.streampipes.model.node.NodeCondition;

public class NodeLiveness {

    private String nodeControllerId;
    private NodeCondition condition;
    private long lastHeartBeatTime;
    private int numFailedLivenessChecks;
    private int maxNumFailedLivenessChecks;

    public NodeLiveness() {
    }

    public NodeLiveness(String nodeControllerId, NodeCondition condition, int maxNumFailedLivenessChecks) {
        this.nodeControllerId = nodeControllerId;
        this.condition = condition;
        this.maxNumFailedLivenessChecks = maxNumFailedLivenessChecks;
    }

    public NodeLiveness(String nodeControllerId, int maxNumFailedLivenessChecks) {
        this.nodeControllerId = nodeControllerId;
        this.maxNumFailedLivenessChecks = maxNumFailedLivenessChecks;
        this.condition = NodeCondition.CREATED;
    }

    public String getNodeControllerId() {
        return nodeControllerId;
    }

    public void setNodeControllerId(String nodeControllerId) {
        this.nodeControllerId = nodeControllerId;
    }

    public NodeCondition getCondition() {
        return condition;
    }

    public void setCondition(NodeCondition condition) {
        this.condition = condition;
    }

    public long getLastHeartBeatTime() {
        return lastHeartBeatTime;
    }

    public void setLastHeartBeatTime(long lastHeartBeatTime) {
        this.lastHeartBeatTime = lastHeartBeatTime;
    }

    public int getNumFailedLivenessChecks() {
        return numFailedLivenessChecks;
    }

    public void setNumFailedLivenessChecks(int numFailedLivenessChecks) {
        this.numFailedLivenessChecks = numFailedLivenessChecks;
    }

    public int getMaxNumFailedLivenessChecks() {
        return maxNumFailedLivenessChecks;
    }

    public void setOnline() {
        this.setCondition(NodeCondition.ONLINE);
        this.setLastHeartBeatTime(System.currentTimeMillis());
        this.resetNumFailedLivenessChecks();
    }

    public boolean reachMaxNumFailedLivenessChecks() {
        return this.numFailedLivenessChecks > this.maxNumFailedLivenessChecks;
    }

    public void increaseFailedChecks() {
        this.numFailedLivenessChecks++;
    }

    public void setOffline() {
        this.setCondition(NodeCondition.OFFLINE);
    }

    public void resetNumFailedLivenessChecks() {
        this.setNumFailedLivenessChecks(0);
    }
}
