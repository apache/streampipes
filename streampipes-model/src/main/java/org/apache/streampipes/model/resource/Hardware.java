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
package org.apache.streampipes.model.resource;


import io.fogsy.empire.annotations.RdfProperty;
import io.fogsy.empire.annotations.RdfsClass;
import org.apache.streampipes.model.shared.annotation.TsModel;
import org.apache.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@RdfsClass(StreamPipes.HARDWARE_REQUIREMENT)
@Entity
@TsModel
public class Hardware extends NodeResourceRequirement {

    @RdfProperty(StreamPipes.HAS_GPU_REQUIREMENT)
    private boolean gpu;

    @RdfProperty(StreamPipes.REQUIRES_CPU_CORES)
    private int cpuCores;

    @RdfProperty(StreamPipes.REQUIRES_MEMORY)
    private long memory;

    @RdfProperty(StreamPipes.REQUIRES_DISK)
    private long disk;

    public Hardware() {
        super();
    }

    public Hardware(Hardware other) {
        super(other);
        this.gpu = other.isGpu();
        this.cpuCores = other.getCpuCores();
        this.memory = other.getMemory();
        this.disk = other.getDisk();
    }

    public Hardware(Hardware other, boolean gpu) {
        super(other);
        this.gpu = gpu;
    }

    public Hardware(NodeResourceRequirement other, boolean gpu, int cpuCores) {
        super(other);
        this.gpu = gpu;
        this.cpuCores = cpuCores;
    }

    public boolean isGpu() {
        return gpu;
    }

    public void setGpu(boolean gpu) {
        this.gpu = gpu;
    }

    public int getCpuCores() {
        return cpuCores;
    }

    public void setCpuCores(int cpuCores) {
        this.cpuCores = cpuCores;
    }

    public long getMemory() {
        return memory;
    }

    public void setMemory(long memory) {
        this.memory = memory;
    }

    public long getDisk() {
        return disk;
    }

    public void setDisk(long disk) {
        this.disk = disk;
    }
}
