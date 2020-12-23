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

import io.fogsy.empire.annotations.RdfProperty;
import io.fogsy.empire.annotations.RdfsClass;
import org.apache.streampipes.model.base.UnnamedStreamPipesEntity;
import org.apache.streampipes.model.shared.annotation.TsModel;
import org.apache.streampipes.vocabulary.StreamPipes;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

@RdfsClass(StreamPipes.NODE_SOFTWARE_RESOURCE)
@Entity
@TsModel
public class SoftwareResource extends UnnamedStreamPipesEntity {

    @RdfProperty(StreamPipes.HAS_OPERATING_SYSTEM)
    private String os;

    @RdfProperty(StreamPipes.HAS_KERNEL_VERSION)
    private String kernelVersion;

    @OneToOne(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty(StreamPipes.HAS_CONTAINER_RUNTIME)
    public ContainerRuntime containerRuntime;

    public SoftwareResource() {
        super();
    }

    public SoftwareResource(SoftwareResource other) {
        super(other);
        this.os = other.getOs();
        this.kernelVersion = other.getKernelVersion();
        this.containerRuntime = other.getContainerRuntime();
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public ContainerRuntime getContainerRuntime() {
        return containerRuntime;
    }

    public void setContainerRuntime(ContainerRuntime containerRuntime) {
        this.containerRuntime = containerRuntime;
    }

    public String getKernelVersion() {
        return kernelVersion;
    }

    public void setKernelVersion(String kernelVersion) {
        this.kernelVersion = kernelVersion;
    }

}
