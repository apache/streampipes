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
package org.apache.streampipes.model.node.resources.software;

import io.fogsy.empire.annotations.RdfProperty;
import io.fogsy.empire.annotations.RdfsClass;
import org.apache.streampipes.vocabulary.StreamPipes;

@RdfsClass(StreamPipes.NVIDIA_CONTAINER_RUNTIME)
public class NvidiaContainerRuntime extends ContainerRuntime {

    @RdfProperty(StreamPipes.HAS_CUDA_DRIVER_VERSION)
    private String cudaDriverVersion;

    @RdfProperty(StreamPipes.HAS_CUDA_RUNTIME_VERSION)
    private String cudaRuntimeVersion;

    public NvidiaContainerRuntime() {
        super();
    }

    public NvidiaContainerRuntime(NvidiaContainerRuntime other) {
        super(other);
    }

    public NvidiaContainerRuntime(String serverVersion, String apiVersion) {
        super(serverVersion, apiVersion);
    }

    public String getCudaDriverVersion() {
        return cudaDriverVersion;
    }

    public void setCudaDriverVersion(String cudaDriverVersion) {
        this.cudaDriverVersion = cudaDriverVersion;
    }

    public String getCudaRuntimeVersion() {
        return cudaRuntimeVersion;
    }

    public void setCudaRuntimeVersion(String cudaRuntimeVersion) {
        this.cudaRuntimeVersion = cudaRuntimeVersion;
    }
}
