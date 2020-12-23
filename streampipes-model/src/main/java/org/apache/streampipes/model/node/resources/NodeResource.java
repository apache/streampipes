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
package org.apache.streampipes.model.node.resources;

import io.fogsy.empire.annotations.RdfProperty;
import io.fogsy.empire.annotations.RdfsClass;
import org.apache.streampipes.model.base.UnnamedStreamPipesEntity;
import org.apache.streampipes.model.node.resources.fielddevice.FieldDeviceAccessResource;
import org.apache.streampipes.model.node.resources.hardware.HardwareResource;
import org.apache.streampipes.model.node.resources.software.SoftwareResource;
import org.apache.streampipes.model.shared.annotation.TsModel;
import org.apache.streampipes.vocabulary.StreamPipes;

import javax.persistence.*;
import java.util.List;

@RdfsClass(StreamPipes.NODE_RESOURCE)
@Entity
@TsModel
public class NodeResource extends UnnamedStreamPipesEntity {

    @OneToOne(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty(StreamPipes.HAS_HARDWARE_RESOURCES)
    private HardwareResource hardwareResource;

    @OneToOne(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty(StreamPipes.HAS_SOFTWARE_RESOURCES)
    private SoftwareResource softwareResource;

    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty(StreamPipes.HAS_FIELD_DEVICE_ACCESS_RESOURCES)
    private List<FieldDeviceAccessResource> fieldDeviceAccessResourceList;

    public NodeResource() {
    }

    public NodeResource(NodeResource other) {
        super(other);
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

    public List<FieldDeviceAccessResource> getFieldDeviceAccessResourceList() {
        return fieldDeviceAccessResourceList;
    }

    public void setFieldDeviceAccessResourceList(List<FieldDeviceAccessResource> fieldDeviceAccessResourceList) {
        this.fieldDeviceAccessResourceList = fieldDeviceAccessResourceList;
    }
}
