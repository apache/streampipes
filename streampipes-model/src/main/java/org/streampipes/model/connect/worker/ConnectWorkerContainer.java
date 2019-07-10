/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.model.connect.worker;

import com.google.gson.annotations.SerializedName;
import org.streampipes.empire.annotations.Namespaces;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.UnnamedStreamPipesEntity;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.grounding.ProtocolDescription;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Namespaces({"sp", "https://streampipes.org/vocabulary/v1/"})
@RdfsClass(StreamPipes.CONNECT_WORKER_CONTAINER)
@Entity
public class ConnectWorkerContainer extends UnnamedStreamPipesEntity {

    @RdfProperty("sp:couchDBId")
    private @SerializedName("_id") String id;

    private @SerializedName("_rev") String rev;

    public ConnectWorkerContainer() {
        super();
        this.adapters = new ArrayList<>();
        this.protocols = new ArrayList<>();
    }

    @RdfProperty("sp:endpointUrl")
    private String endpointUrl;

    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty("sp:protocols")
    private List<ProtocolDescription> protocols;

//    @OneToMany(fetch = FetchType.EAGER,
//            cascade = {CascadeType.ALL})
//    @RdfProperty("sp:adapters")
    private List<AdapterDescription> adapters;

    public ConnectWorkerContainer(String endpointUrl, List<ProtocolDescription> protocols, List<AdapterDescription> adapters) {
        this.endpointUrl = endpointUrl;
        this.protocols = protocols;
        this.adapters = adapters;
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
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
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }
}
