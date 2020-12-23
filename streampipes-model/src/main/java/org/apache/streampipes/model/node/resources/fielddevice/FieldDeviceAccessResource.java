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
package org.apache.streampipes.model.node.resources.fielddevice;

import io.fogsy.empire.annotations.RdfProperty;
import io.fogsy.empire.annotations.RdfsClass;
import org.apache.streampipes.model.base.UnnamedStreamPipesEntity;
import org.apache.streampipes.vocabulary.StreamPipes;

@RdfsClass(StreamPipes.NODE_FIELD_DEVICE_ACCESS_RESOURCE)
public class FieldDeviceAccessResource extends UnnamedStreamPipesEntity {

    @RdfProperty(StreamPipes.FIELD_DEVICE_NAME)
    private String deviceName;

    @RdfProperty(StreamPipes.FIELD_DEVICE_TYPE)
    private String deviceType;

    @RdfProperty(StreamPipes.FIELD_DEVICE_CONNECTION_TYPE)
    private String connectionType;

    @RdfProperty(StreamPipes.FIELD_DEVICE_CONNECTION_STRING)
    private String connectionString;

    public FieldDeviceAccessResource() {
        super();
    }

    public FieldDeviceAccessResource(FieldDeviceAccessResource other) {
        super(other);
    }

    public FieldDeviceAccessResource(String deviceName, String deviceType, String connectionType,
                                     String connectionString) {
        this.deviceName = deviceName;
        this.deviceType = deviceType;
        this.connectionType = connectionType;
        this.connectionString = connectionString;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }
}
