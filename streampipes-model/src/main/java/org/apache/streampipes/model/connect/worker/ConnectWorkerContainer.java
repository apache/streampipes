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

package org.apache.streampipes.model.connect.worker;

import com.google.gson.annotations.SerializedName;
import org.apache.streampipes.model.base.UnnamedStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.apache.streampipes.model.util.ElementIdGenerator;

import java.util.ArrayList;
import java.util.List;

public class ConnectWorkerContainer extends UnnamedStreamPipesEntity {

    private @SerializedName("_rev") String rev;

    private String serviceGroup;

    private List<ProtocolDescription> protocols;

    private List<AdapterDescription> adapters;

    public ConnectWorkerContainer() {
        super();
        this.adapters = new ArrayList<>();
        this.protocols = new ArrayList<>();
    }

    public ConnectWorkerContainer(String serviceGroup,
                                  List<ProtocolDescription> protocols,
                                  List<AdapterDescription> adapters) {
        super();
        this.elementId = ElementIdGenerator.makeElementIdFromAppId(serviceGroup);
        this.serviceGroup = serviceGroup;
        this.protocols = protocols;
        this.adapters = adapters;
    }

    public String getServiceGroup() {
        return serviceGroup;
    }

    public void setServiceGroup(String serviceGroup) {
        this.serviceGroup = serviceGroup;
    }

    public List<ProtocolDescription> getProtocols() {
        return protocols;
    }

    public void setProtocols(List<ProtocolDescription> protocols) {
        this.protocols = protocols;
    }

    public List<AdapterDescription> getAdapters() {
        return adapters;
    }

    public void setAdapters(List<AdapterDescription> adapters) {
        this.adapters = adapters;
    }

    public String getId() {
        return this.elementId;
    }

    public void setId(String id) {
        this.elementId = id;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }
}
