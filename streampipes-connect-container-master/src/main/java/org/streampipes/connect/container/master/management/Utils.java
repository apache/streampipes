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

import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.GenericAdapterDescription;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;

public class Utils {
    private WorkerAdministrationManagement workerAdministrationManagement;

    public Utils() {
        this.workerAdministrationManagement = new WorkerAdministrationManagement();
    }

    public static String addUserNameToApi(String url, String userName) {
        return  url + "api/v1/" + userName + "/";
    }

    public String getWorkerUrl(AdapterDescription adapterDescription) {
        String id = "";

        if (adapterDescription instanceof GenericAdapterDescription) {
            id = ((GenericAdapterDescription) (adapterDescription)).getProtocolDescription().getAppId();
        } else {
            id = adapterDescription.getAppId();
        }

        return this.workerAdministrationManagement.getWorkerUrl(id);
    }

    public String getWorkerUrl(ProtocolDescription protocolDescription) {
        String id =  protocolDescription.getAppId();

        return this.workerAdministrationManagement.getWorkerUrl(id);
    }


    public String getWorkerUrlById(String id) {
        return this.workerAdministrationManagement.getWorkerUrl(id);
    }
}
