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

package org.apache.streampipes.connect.container.master.management;

import org.apache.streampipes.connect.api.exception.AdapterException;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.AdapterDescriptionList;
import org.apache.streampipes.model.connect.adapter.GenericAdapterSetDescription;
import org.apache.streampipes.model.connect.adapter.GenericAdapterStreamDescription;
import org.apache.streampipes.model.util.Cloner;
import org.apache.streampipes.storage.api.IAdapterTemplateStorage;
import org.apache.streampipes.storage.couchdb.impl.AdapterTemplateStorageImpl;

import java.util.List;
import java.util.UUID;


public class AdapterTemplateMasterManagement {

    private IAdapterTemplateStorage adapterTemplateStorage;

    public AdapterTemplateMasterManagement() {
        this.adapterTemplateStorage = new AdapterTemplateStorageImpl();

    }

    public AdapterTemplateMasterManagement(IAdapterTemplateStorage adapterTemplateStorage) {
        this.adapterTemplateStorage = adapterTemplateStorage;
    }

    public String addAdapterTemplate(AdapterDescription adapterDescription) throws AdapterException {

//        String uri = "http://streampipes.org/adapter/template/" + UUID.randomUUID().toString();
        adapterDescription = new Cloner().adapterDescription(adapterDescription);

        String uri = adapterDescription.getUri() + UUID.randomUUID().toString();
        adapterDescription.setUri(uri);
        adapterDescription.setElementId(uri);

        if (adapterDescription instanceof GenericAdapterSetDescription) {
            String id = ((GenericAdapterSetDescription) adapterDescription).getFormatDescription().getElementId();
            id = id + "/" + UUID.randomUUID().toString();
            ((GenericAdapterSetDescription) adapterDescription).getFormatDescription().setElementId(id);

            id = ((GenericAdapterSetDescription) adapterDescription).getProtocolDescription().getElementId();
            id = id + "/" + UUID.randomUUID().toString();
            ((GenericAdapterSetDescription) adapterDescription).getProtocolDescription().setElementId(id);

            id = ((GenericAdapterSetDescription) adapterDescription).getDataSet().getElementId();
            id = id + "/" + UUID.randomUUID().toString();
            ((GenericAdapterSetDescription) adapterDescription).getDataSet().setElementId(id);
        }

        if (adapterDescription instanceof GenericAdapterStreamDescription) {
            String id = ((GenericAdapterStreamDescription) adapterDescription).getFormatDescription().getElementId();
            id = id + "/" + UUID.randomUUID().toString();
            ((GenericAdapterStreamDescription) adapterDescription).getFormatDescription().setElementId(id);

            id = ((GenericAdapterStreamDescription) adapterDescription).getProtocolDescription().getElementId();
            id = id + "/" + UUID.randomUUID().toString();
            ((GenericAdapterStreamDescription) adapterDescription).getProtocolDescription().setElementId(id);

            id = ((GenericAdapterStreamDescription) adapterDescription).getDataStream().getElementId();
            id = id + "/" + UUID.randomUUID().toString();
            ((GenericAdapterStreamDescription) adapterDescription).getDataStream().setElementId(id);
        }

        this.adapterTemplateStorage.storeAdapterTemplate(adapterDescription);
        return adapterDescription.getElementId();
    }


    public AdapterDescriptionList getAllAdapterTemplates() throws AdapterException {
        List<AdapterDescription> adapterDescriptions = this.adapterTemplateStorage.getAllAdapterTemplates();
        AdapterDescriptionList result = new AdapterDescriptionList();
        result.setList(adapterDescriptions);

        return result;
    }

    public void deleteAdapterTemplates(String id) throws AdapterException {
        this.adapterTemplateStorage.deleteAdapterTemplate(id);
    }

    public void setAdapterTemplateStorage(IAdapterTemplateStorage adapterTemplateStorage) {
        this.adapterTemplateStorage = adapterTemplateStorage;
    }
}
