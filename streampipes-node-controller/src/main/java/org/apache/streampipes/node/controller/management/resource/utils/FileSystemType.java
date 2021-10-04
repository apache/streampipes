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
package org.apache.streampipes.node.controller.management.resource.utils;

public enum FileSystemType {
    SDA("/dev/sda"),
    NVME("/dev/nvme"),
    DISK("/dev/disk"),
    ROOT("/dev/root"),
    MMCBLK("/dev/mmcblk0p1"),
    SDB("/dev/sdb"),
    MAPPER("/dev/mapper");

    private final String name;

    FileSystemType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
