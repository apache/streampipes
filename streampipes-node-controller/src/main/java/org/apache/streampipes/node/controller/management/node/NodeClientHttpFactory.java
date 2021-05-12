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
package org.apache.streampipes.node.controller.management.node;

import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.node.controller.utils.HttpRequest;

public class NodeClientHttpFactory {

    private static final String BASE_ROUTE = "/streampipes-backend/api/v2/users/admin@streampipes.org/nodes";

    public static boolean execute(NodeInfoDescription node, NodeLifeCycleType type) {
        switch (type) {
            case REGISTER:
                return new NodeClientHttpHandler(node, BASE_ROUTE, HttpRequest.POST).execute();
            case UPDATE:
                return new NodeClientHttpHandler(node, BASE_ROUTE + "/sync", HttpRequest.POST).execute();
            default:
                return false;
        }
    }
}
