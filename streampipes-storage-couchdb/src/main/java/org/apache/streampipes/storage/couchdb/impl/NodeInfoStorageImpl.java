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
package org.apache.streampipes.storage.couchdb.impl;

import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.storage.api.INodeInfoStorage;
import org.apache.streampipes.storage.couchdb.dao.AbstractDao;
import org.apache.streampipes.storage.couchdb.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class NodeInfoStorageImpl extends AbstractDao<NodeInfoDescription> implements INodeInfoStorage {
    private static final Logger LOG = LoggerFactory.getLogger(NodeInfoStorageImpl.class.getCanonicalName());

    public NodeInfoStorageImpl() {
        super(Utils::getCouchDbNodeClient, NodeInfoDescription.class);
    }

    @Override
    public List<NodeInfoDescription> getAllNodes() {
        return findAll();
    }

    @Override
    public List<NodeInfoDescription> getAllActiveNodes() {
        List<NodeInfoDescription> allNodes = getAllNodes();
        return allNodes.stream()
                .filter(NodeInfoDescription::isActive)
                .collect(Collectors.toList());
    }

    @Override
    public void storeNode(NodeInfoDescription desc) {
        LOG.info("Store new node description with node id={}, url={}", desc.getNodeControllerId(),
                desc.getHostname() + ":" + desc.getPort());
        persist(desc);
    }

    @Override
    public void updateNode(NodeInfoDescription desc) {
        LOG.info("Update node description for node id={}, url={}", desc.getNodeControllerId(),
                desc.getHostname() + ":" + desc.getPort());

        Optional<NodeInfoDescription> storedNode = getNode(desc.getNodeControllerId());

        if (storedNode.isPresent()) {
            desc.setId(storedNode.get().getId());
            desc.setRev(storedNode.get().getRev());

            update(desc);
        }
    }

    @Override
    public Optional<NodeInfoDescription> getNode(String nodeControllerId) {
        return findAll().stream()
                .filter(n -> n.getNodeControllerId().equals(nodeControllerId))
                .findAny();
    }

    @Override
    public void deleteNode(String nodeControllerId) {
        LOG.info("Delete node with node id={}", nodeControllerId);
        Optional<NodeInfoDescription> storedNode = getNode(nodeControllerId);

        storedNode.ifPresent(nodeInfoDescription -> delete(nodeInfoDescription.getId()));
    }
}
