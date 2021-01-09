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
package org.apache.streampipes.sdk.builder;


import org.apache.streampipes.model.resource.NodeResourceRequirement;
import org.apache.streampipes.sdk.helpers.CollectedResourceRequirements;

import java.util.ArrayList;
import java.util.List;

public class ResourceRequirementsBuilder {

    private final List<NodeResourceRequirement> resourceRequirements;

    public static ResourceRequirementsBuilder create() {
        return new ResourceRequirementsBuilder();
    }

    public ResourceRequirementsBuilder() {
        this.resourceRequirements = new ArrayList<>();
    }

    public ResourceRequirementsBuilder requiredProperty(NodeResourceRequirement nrp) {
        this.resourceRequirements.add(nrp);
        return this;
    }

    public CollectedResourceRequirements build() {
        return new CollectedResourceRequirements(this.resourceRequirements);
    }
}
