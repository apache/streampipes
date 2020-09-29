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

import org.apache.streampipes.model.node.resources.hardware.HardwareResource;
import org.apache.streampipes.model.node.resources.interfaces.AccessibleSensorActuatorResource;
import org.apache.streampipes.model.node.resources.software.SoftwareResource;
import org.apache.streampipes.model.shared.annotation.TsModel;

import java.util.List;

@TsModel
public class NodeResources {
    public HardwareResource hardwareResource;
    public SoftwareResource softwareResource;
    public List<AccessibleSensorActuatorResource> accessibleSensorActuatorResource;

    public NodeResources() {
    }

    public HardwareResource getHardwareResource() {
        return hardwareResource;
    }

    public void setHardwareResource(HardwareResource hardwareResource) {
        this.hardwareResource = hardwareResource;
    }

    public SoftwareResource getSoftwareResource() {
        return softwareResource;
    }

    public void setSoftwareResource(SoftwareResource softwareResource) {
        this.softwareResource = softwareResource;
    }

    public List<AccessibleSensorActuatorResource> getAccessibleSensorActuatorResource() {
        return accessibleSensorActuatorResource;
    }

    public void setAccessibleSensorActuatorResource(List<AccessibleSensorActuatorResource> accessibleSensorActuatorResource) {
        this.accessibleSensorActuatorResource = accessibleSensorActuatorResource;
    }
}
