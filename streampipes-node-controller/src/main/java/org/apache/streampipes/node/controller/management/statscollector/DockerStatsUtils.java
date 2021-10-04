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
import com.spotify.docker.client.messages.NetworkStats;
import org.apache.streampipes.model.Tuple2;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class DockerStatsUtils {

    public static double getMemUsageInBytes(ContainerStats containerStats) {
        return containerStats.memoryStats().usage().doubleValue();
    }

    public static float getCpuPercent(ContainerStats containerStats) {
        return calculateCpuUsageInPercent(
                containerStats.cpuStats().cpuUsage().totalUsage().floatValue(),
                containerStats.precpuStats().cpuUsage().totalUsage().floatValue(),
                containerStats.cpuStats().systemCpuUsage().floatValue(),
                containerStats.precpuStats().systemCpuUsage().floatValue(),
                containerStats.cpuStats().cpuUsage().percpuUsage().size());
    }

    public static float calculateCpuUsageInPercent(float cpuCurrentTotal, float cpuPreviousTotal,
                                              float cpuSystemCurrentUsage,
                                             float cpuSystemPreviousUsage, int numCores) {
        float cpuPercent = 0.0f;
        float cpuDelta = cpuCurrentTotal - cpuPreviousTotal;
        float systemDelta = cpuSystemCurrentUsage - cpuSystemPreviousUsage;

        if (cpuDelta > 0.0 && systemDelta > 0.0) {
            cpuPercent = (cpuDelta / systemDelta) * (numCores * 100F);
        }
        return cpuPercent;
    }

    public static Tuple2<Long,Long> calculateNetworkIo(ContainerStats containerStats) {
        Map<String, NetworkStats> networks = containerStats.networks();
        AtomicLong rx = new AtomicLong(0L);
        AtomicLong tx = new AtomicLong(0L);
        networks.values().forEach(n -> {
            rx.addAndGet(n.rxBytes());
            tx.addAndGet(n.txBytes());
        });
        return new Tuple2<>(rx.longValue(),tx.longValue());
    }

    public static String humanReadableByteCountBin(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.1f %ciB", value / 1024.0, ci.current());
    }
}
