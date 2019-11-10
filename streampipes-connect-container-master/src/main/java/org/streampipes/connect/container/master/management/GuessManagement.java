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

package org.streampipes.connect.container.master.management;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.connect.adapter.exception.AdapterException;
import org.streampipes.connect.adapter.exception.ParseException;
import org.streampipes.model.client.messages.ErrorMessageLd;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.rest.shared.util.JsonLdUtils;
import org.streampipes.vocabulary.StreamPipes;

import java.io.IOException;

public class GuessManagement {

    private String errorMessage = "Sorry, something went wrong! Hit the feedback button (top right corner) to ask for help. If you think you've found a bug, fill an issue on our Github Page";

    private static Logger LOG = LoggerFactory.getLogger(GuessManagement.class);

    private WorkerAdministrationManagement workerAdministrationManagement;

    public GuessManagement() {
        this.workerAdministrationManagement = new WorkerAdministrationManagement();
    }

    public GuessSchema guessSchema(AdapterDescription adapterDescription) throws AdapterException, ParseException {
        String workerUrl = new Utils().getWorkerUrl(adapterDescription);

        workerUrl = workerUrl + "api/v1/admin@streampipes.de/worker/guess/schema";



        String ad = JsonLdUtils.toJsonLD(adapterDescription);

        try {

            LOG.info("Guess schema at: " + workerUrl);
            String responseString = Request.Post(workerUrl)
                    .bodyString(ad, ContentType.APPLICATION_JSON)
                    .connectTimeout(1000)
                    .socketTimeout(100000)
                    .execute().returnContent().asString();

            GuessSchema guessSchema = JsonLdUtils.fromJsonLd(responseString, GuessSchema.class);

            if (guessSchema.getEventSchema() != null) {
                return guessSchema;
            } else {
                ErrorMessageLd errorMessageLd = JsonLdUtils.fromJsonLd(responseString, ErrorMessageLd.class, StreamPipes.ERROR_MESSAGE);
                if (errorMessageLd.getNotifications() != null && errorMessageLd.getNotifications().get(0) != null) {
                    throw new AdapterException(errorMessageLd.getNotifications().get(0).getTitle());
                } else {
                    throw new AdapterException("There was an error while guessing the schema in the worker with the URL: " + workerUrl + "\n" +
                            errorMessage);
                }


            }


        } catch (IOException e) {
            e.printStackTrace();
            throw new AdapterException("Connect Worker: " + workerUrl + " is currently not available.\n" +
                    errorMessage);
        }
    }

    public void guessFormat() {
        // TODO implement
    }


    public void  guessFormatDescription() {
        // TODO implement
    }



}
