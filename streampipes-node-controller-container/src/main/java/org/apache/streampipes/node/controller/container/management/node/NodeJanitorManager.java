package org.apache.streampipes.node.controller.container.management.node;/*
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

import org.apache.streampipes.node.controller.container.config.NodeControllerConfig;
import org.apache.streampipes.node.controller.container.management.pe.DockerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NodeJanitorManager {

    private static final Logger LOG =
            LoggerFactory.getLogger(NodeJanitorManager.class.getCanonicalName());

    private ScheduledExecutorService scheduledExecutorService = null;

    private static NodeJanitorManager instance = null;

    private NodeJanitorManager() {}

    public static NodeJanitorManager getInstance() {
        if (instance == null) {
            synchronized (NodeJanitorManager.class) {
                if (instance == null)
                    instance = new NodeJanitorManager();
            }
        }
        return instance;
    }

    // regularly clean dangling docker images
    public void run() {
        LOG.info("Create Janitor scheduler");

        scheduledExecutorService =  Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(pruneDocker, 30, NodeControllerConfig.INSTANCE.getPruningFreq(), TimeUnit.MINUTES);
    }

    private final Runnable pruneDocker = () -> {
        LOG.info("Clean up dangling images");
        DockerUtils.prune();
    };


}
