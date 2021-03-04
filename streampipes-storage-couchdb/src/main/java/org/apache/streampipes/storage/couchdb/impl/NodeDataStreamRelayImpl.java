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

import org.apache.streampipes.model.eventrelay.SpDataStreamRelayContainer;
import org.apache.streampipes.storage.api.INodeDataStreamRelay;
import org.apache.streampipes.storage.couchdb.dao.AbstractDao;
import org.apache.streampipes.storage.couchdb.utils.Utils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class NodeDataStreamRelayImpl extends AbstractDao<SpDataStreamRelayContainer> implements INodeDataStreamRelay {

    public NodeDataStreamRelayImpl() {
        super(Utils::getCouchDbNodeDataStreamRelayClient, SpDataStreamRelayContainer.class);
    }

    @Override
    public List<SpDataStreamRelayContainer> getAll() {
        return findAll();
    }

    @Override
    public List<SpDataStreamRelayContainer> getAllByNodeControllerId(String id) {
        return getAll().stream()
                .filter(e -> e.getDeploymentTargetNodeId().equals(id))
                .collect(Collectors.toList());
    }

    @Override
    public void addRelayContainer(SpDataStreamRelayContainer relayContainer) {
        persist(relayContainer);
    }

    @Override
    public Optional<SpDataStreamRelayContainer> getRelayContainerById(String s) {
        return getAll().stream()
                .filter(e -> e.getRunningStreamRelayInstanceId().equals(s))
                .findAny();
    }

    @Override
    public void updateRelayContainer(SpDataStreamRelayContainer relayContainer) {
        Optional<SpDataStreamRelayContainer> rc =
                getRelayContainerById(relayContainer.getRunningStreamRelayInstanceId());
        if(rc.isPresent()) {
            relayContainer.setCouchDbId(rc.get().getCouchDbId());
            relayContainer.setCouchDbRev(rc.get().getCouchDbRev());

            update(relayContainer);
        }
    }

    @Override
    public void deleteRelayContainer(SpDataStreamRelayContainer relayContainer) {
        Optional<SpDataStreamRelayContainer> rc = getRelayContainerById(relayContainer.getRunningStreamRelayInstanceId());
        rc.ifPresent(r -> delete(r.getCouchDbId()));
    }
}
