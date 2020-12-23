package org.apache.streampipes.model.node.resources.hardware;/*
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

@RdfsClass(StreamPipes.NODE_HARDWARE_RESOURCE)
@Entity
@TsModel
public class HardwareResource extends UnnamedStreamPipesEntity {

    @OneToOne(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty(StreamPipes.HAS_CPU)
    private CPU cpu;

    @OneToOne(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty(StreamPipes.HAS_MEMORY)
    private MEM memory;

    @OneToOne(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty(StreamPipes.HAS_DISK)
    private DISK disk;

    @OneToOne(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty(StreamPipes.HAS_GPU)
    private GPU gpu;

    public HardwareResource() {
        super();
    }

    public HardwareResource(HardwareResource other) {
        super(other);
    }

    public CPU getCpu() {
        return cpu;
    }

    public void setCpu(CPU cpu) {
        this.cpu = cpu;
    }

    public MEM getMemory() {
        return memory;
    }

    public void setMemory(MEM memory) {
        this.memory = memory;
    }

    public DISK getDisk() {
        return disk;
    }

    public void setDisk(DISK disk) {
        this.disk = disk;
    }

    public GPU getGpu() {
        return gpu;
    }

    public void setGpu(GPU gpu) {
        this.gpu = gpu;
    }
}
