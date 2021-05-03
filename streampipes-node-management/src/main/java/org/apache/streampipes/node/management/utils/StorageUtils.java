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
package org.apache.streampipes.node.management.utils;

import org.apache.streampipes.model.eventrelay.SpDataStreamRelayContainer;
import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.storage.api.INodeDataStreamRelay;
import org.apache.streampipes.storage.api.INodeInfoStorage;
import org.apache.streampipes.storage.api.IPipelineStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class StorageUtils {
    private static final Logger LOG = LoggerFactory.getLogger(StorageUtils.class.getCanonicalName());

    public static INodeInfoStorage persistentNodeAPI() {
        return StorageDispatcher.INSTANCE.getNoSqlStore().getNodeStorage();
    }

    public static INodeDataStreamRelay persistentRelayAPI(){
        return StorageDispatcher.INSTANCE.getNoSqlStore().getNodeDataStreamRelayStorage();
    }

    public static IPipelineStorage persistentPipelineAPI() {
        return StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI();
    }

    public static void storeNode(NodeInfoDescription node) {
        persistentNodeAPI().storeNode(node);
    }

    public static Optional<NodeInfoDescription> getNode(String nodeControllerId){
        return persistentNodeAPI().getNode(nodeControllerId);
    }

    public static List<NodeInfoDescription> getAllNodes() {
        return StorageUtils.persistentNodeAPI().getAllNodes();
    }

    public static Optional<NodeInfoDescription> getLatestNodeOrElseEmpty(String nodeControllerId) {
        return StorageUtils.persistentNodeAPI().getAllNodes().stream()
                .filter(n -> n.getNodeControllerId().equals(nodeControllerId))
                .findAny();
    }

    public static void updateNode(NodeInfoDescription node){
        persistentNodeAPI().updateNode(node);
    }

    public static void deleteNode(String nodeControllerId){
        persistentNodeAPI().deleteNode(nodeControllerId);
    }

    public static List<SpDataStreamRelayContainer> getAllRelaysFromNode(String nodeControllerId) {
        return persistentRelayAPI().getAllByNodeControllerId(nodeControllerId);
    }

    public static void activateNode(String nodeControllerId) {
        persistentNodeAPI().activateNode(nodeControllerId);
    }

    public static void deactivateNode(String nodeControllerId) {
        persistentNodeAPI().deactivateNode(nodeControllerId);
    }
}
