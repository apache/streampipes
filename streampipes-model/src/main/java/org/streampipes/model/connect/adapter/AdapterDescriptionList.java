/*
 * Copyright 2018 FZI Forschungszentrum Informatik
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

package org.streampipes.model.connect.adapter;

import org.streampipes.empire.annotations.Namespaces;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.UnnamedStreamPipesEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Namespaces({"sp", "https://streampipes.org/vocabulary/v1/\""})
@RdfsClass("sp:AdapterDescriptionList")
@Entity
public class AdapterDescriptionList extends UnnamedStreamPipesEntity {
    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty("sp:list")
    private List<AdapterDescription> list;

    public AdapterDescriptionList() {
        super();
        list = new ArrayList<>();
    }

    public List<AdapterDescription> getList() {
        return list;
    }

    public void setList(List<AdapterDescription> list) {
        this.list = list;
    }
}
