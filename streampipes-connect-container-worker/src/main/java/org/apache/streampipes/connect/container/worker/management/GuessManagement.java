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

package org.apache.streampipes.connect.container.worker.management;

import org.apache.streampipes.connect.api.IAdapter;
import org.apache.streampipes.connect.api.exception.AdapterException;
import org.apache.streampipes.connect.api.exception.ParseException;
import org.apache.streampipes.connect.container.worker.utils.AdapterUtils;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;

public class GuessManagement {

    private static Logger LOG = LoggerFactory.getLogger(GuessManagement.class);

    public GuessSchema guessSchema(AdapterDescription adapterDescription) throws AdapterException, ParseException {

        LOG.info("Start guessing schema for: " + adapterDescription.getAppId());
        IAdapter adapter = AdapterUtils.setAdapter(adapterDescription);

        GuessSchema guessSchema;
        try {
            guessSchema = adapter.getSchema(adapterDescription);

            for (int i = 0; i < guessSchema.getEventSchema().getEventProperties().size(); i++) {
                guessSchema.getEventSchema().getEventProperties().get(i).setIndex(i);
            }

            LOG.info("Successfully guessed schema for: " + adapterDescription.getAppId());
        } catch (ParseException e) {
            LOG.error(e.toString());

            String errorClass = "";
            Optional<StackTraceElement> stackTraceElement = Arrays.stream(e.getStackTrace()).findFirst();
            if(stackTraceElement.isPresent()) {
                String[] errorClassLong = stackTraceElement.get().getClassName().split("\\.");
                errorClass = errorClassLong[errorClassLong.length - 1] + ": ";
            }

            throw new ParseException(errorClass + e.getMessage());
        } catch (Exception e) {
            throw new AdapterException(e.getMessage(), e);
        }

        return guessSchema;

    }

}
