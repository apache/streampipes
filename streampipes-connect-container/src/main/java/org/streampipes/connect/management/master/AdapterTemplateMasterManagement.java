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

package org.streampipes.connect.management.master;

import org.streampipes.connect.exception.AdapterException;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.adapter.AdapterDescriptionList;
import org.streampipes.storage.api.IAdapterTemplateStorage;
import org.streampipes.storage.couchdb.impl.AdapterTemplateStorageImpl;

import java.util.List;


public class AdapterTemplateMasterManagement {

    private IAdapterTemplateStorage adapterTemplateStorage;

    public AdapterTemplateMasterManagement() {
         this.adapterTemplateStorage = new AdapterTemplateStorageImpl();

    }

    public AdapterTemplateMasterManagement(IAdapterTemplateStorage adapterTemplateStorage) {
        this.adapterTemplateStorage = adapterTemplateStorage;
    }

    public String addAdapterTemplate(AdapterDescription adapterDescription) throws AdapterException {
        this.adapterTemplateStorage.storeAdapterTemplate(adapterDescription);
        return adapterDescription.getId();
    }


    public AdapterDescriptionList getAllAdapterTemplates() throws AdapterException {
        List<AdapterDescription> adapterDescriptions = this.adapterTemplateStorage.getAllAdapterTemplates();
        AdapterDescriptionList result = new AdapterDescriptionList();
        result.setList(adapterDescriptions);

        return result;
    }

    public void setAdapterTemplateStorage(IAdapterTemplateStorage adapterTemplateStorage) {
        this.adapterTemplateStorage = adapterTemplateStorage;
    }
}
