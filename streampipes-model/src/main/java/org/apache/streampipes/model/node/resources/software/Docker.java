package org.apache.streampipes.model.node.resources.software;/*
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

public class Docker {

    public boolean hasDocker;
    public boolean hasNvidiaRuntime;
    public String serverVersion;
    public String apiVersion;

    public boolean hasDocker() {
        return hasDocker;
    }

    public void setHasDocker(boolean hasDocker) {
        this.hasDocker = hasDocker;
    }

    public boolean hasNvidiaRuntime() {
        return hasNvidiaRuntime;
    }

    public void setHasNvidiaRuntime(boolean hasNvidiaRuntime) {
        this.hasNvidiaRuntime = hasNvidiaRuntime;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }
}
