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

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.connect.adapter.exception.AdapterException;
import org.streampipes.connect.adapter.exception.ParseException;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.adapter.GenericAdapterDescription;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.rest.shared.util.JsonLdUtils;

import java.io.IOException;

public class GuessManagement {

    private static Logger LOG = LoggerFactory.getLogger(GuessManagement.class);

    private WorkerAdministrationManagement workerAdministrationManagement;

    public GuessManagement() {
       this.workerAdministrationManagement = new WorkerAdministrationManagement();
    }

    public GuessSchema guessSchema(AdapterDescription adapterDescription) throws AdapterException, ParseException {
        String protocolId = ((GenericAdapterDescription) (adapterDescription)).getProtocolDescription().getAppId();

        String workerUrl = this.workerAdministrationManagement.getWorkerUrl(protocolId);
        // Make REST call to worker
        workerUrl = workerUrl + "/api/v1/admin@streampipes.de/worker/guess/schema";

        String ad = JsonLdUtils.toJsonLD(adapterDescription);

        try {
            String responseString = Request.Post(workerUrl)
                    .bodyString(ad, ContentType.APPLICATION_JSON)
                    .connectTimeout(1000)
                    .socketTimeout(100000)
                    .execute().returnContent().asString();

            return JsonLdUtils.fromJsonLd(responseString, GuessSchema.class);

        } catch (IOException e) {
            e.printStackTrace();
            throw new AdapterException("Could not guess schema at: " + workerUrl);
        }
    }

    public void guessFormat() {
        // TODO implement
    }


    public void  guessFormatDescription() {
        // TODO implement
    }



}
