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
package org.apache.streampipes.node.controller.management.statscollector;

import com.spotify.docker.client.messages.ContainerStats;
import org.apache.streampipes.logging.evaluation.EvaluationLogger;
import org.apache.streampipes.node.controller.config.NodeConfiguration;
import org.apache.streampipes.node.controller.management.orchestrator.docker.utils.DockerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DockerStatsCollector {

    private static final Logger LOG = LoggerFactory.getLogger(DockerStatsCollector.class.getCanonicalName());
    private static final String LOGGING_TOPIC = "container/stats/" + NodeConfiguration.getNodeHost();

    private static DockerStatsCollector instance = null;

    private DockerStatsCollector() {}

    public static DockerStatsCollector getInstance() {
        if (instance == null) {
            synchronized (DockerStatsCollector.class) {
                if (instance == null)
                    instance = new DockerStatsCollector();
            }
        }
        return instance;
    }

    public void run() {
        LOG.debug("Create Docker stats scheduler");
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(collect, 0, 1, TimeUnit.SECONDS);

        Object[] header = new Object[]{
                "timestamp",
                "containerName",
                "cpuPercent",
                "memPercent",
                "memUsageInBytes",
                "memUsageHumanReadable",
                "memTotalInBytes",
                "memTotalHumanReadable"};

        EvaluationLogger.getInstance().logMQTT(LOGGING_TOPIC, header);
    }

    private final Runnable collect = () -> {
        LOG.debug("Collect Docker stats");
        Map<String, ContainerStats> stats = DockerUtils.getInstance().collectStats();
        long timestamp = System.currentTimeMillis();

        stats.forEach((containerName, containerStats) -> {

            float cpuPercent = DockerStatsUtils.getCpuPercent(containerStats);
            double memUsageInBytes = DockerStatsUtils.getMemUsageInBytes(containerStats);
            double memTotal = containerStats.memoryStats().limit();
            double memPercent = (memUsageInBytes / memTotal) * 100.0;

            Object[] collectedStats = new Object[]{
                    timestamp,
                    containerName,
                    cpuPercent,
                    memPercent,
                    memUsageInBytes,
                    DockerStatsUtils.humanReadableByteCountBin((long) memUsageInBytes),
                    memTotal,
                    DockerStatsUtils.humanReadableByteCountBin((long) memTotal)};

            EvaluationLogger.getInstance().logMQTT(LOGGING_TOPIC, collectedStats);
        });
    };
}
