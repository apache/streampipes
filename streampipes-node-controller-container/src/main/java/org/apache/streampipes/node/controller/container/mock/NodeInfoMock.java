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
package org.apache.streampipes.node.controller.container.mock;

import org.apache.streampipes.model.node.NodeInfo;
import org.apache.streampipes.model.node.NodeInfoBuilder;

import java.util.Arrays;

public class NodeInfoMock {

    public static NodeInfo mockNodeInfo() {
        return NodeInfoBuilder.create("my-id")
                .withNodeName("Jetson Nano")
                .withNodeDescription("The Jetson Nano node description")
                .withJmsTransportProtocol("ipe-girlitz.fzi.de", 61616)
                .withSupportedPipelineElements(Arrays.asList("org.streampipes.dashboard"))
                .build();
    }
}
