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
package org.apache.streampipes.model.node.container;

import java.util.ArrayList;
import java.util.List;

public class ContainerVolumesBuilder {
    private final List<String> volumes;

    public ContainerVolumesBuilder() {
        this.volumes = new ArrayList<>();
    }

    public static ContainerVolumesBuilder create() {
        return new ContainerVolumesBuilder();
    }

    public ContainerVolumesBuilder addNodeEnvs(List<String> volumes) {
        this.volumes.addAll(volumes);
        return this;
    }

    public ContainerVolumesBuilder add(String key, String value, boolean readOnly) {
        if (readOnly) {
            this.volumes.add(String.format("%s:%s:%s", key, value, "ro"));
        } else {
            this.volumes.add(String.format("%s:%s", key, value));
        }
        return this;
    }

    public List<String> build() {
        return volumes;
    }
}
