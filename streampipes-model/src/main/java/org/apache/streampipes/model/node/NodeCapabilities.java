package org.apache.streampipes.model.node;/*
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

import org.apache.streampipes.model.node.capabilities.hardware.Hardware;
import org.apache.streampipes.model.node.capabilities.interfaces.Interfaces;
import org.apache.streampipes.model.node.capabilities.software.Software;

import java.util.List;

public class NodeCapabilities {
    public Hardware hardware;
    public Software software;
    public List<Interfaces> interfaces;

    public NodeCapabilities() {
    }

    public Hardware getHardware() {
        return hardware;
    }

    public void setHardware(Hardware hardware) {
        this.hardware = hardware;
    }

    public Software getSoftware() {
        return software;
    }

    public void setSoftware(Software software) {
        this.software = software;
    }

    public List<Interfaces> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<Interfaces> interfaces) {
        this.interfaces = interfaces;
    }
}
