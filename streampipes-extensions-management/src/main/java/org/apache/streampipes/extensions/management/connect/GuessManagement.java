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

package org.apache.streampipes.extensions.management.connect;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.commons.exceptions.connect.ParseException;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.extensions.management.init.IDeclarersSingleton;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.sdk.extractor.AdapterParameterExtractor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;

public class GuessManagement {

  private static final Logger LOG = LoggerFactory.getLogger(GuessManagement.class);

  private IAdapterGuessSchemaContext guessSchemaContext;

  public GuessManagement() {
  }

  public GuessManagement(IAdapterGuessSchemaContext guessSchemaContext) {
    this.guessSchemaContext = guessSchemaContext;
  }

  public GuessSchema guessSchema(AdapterDescription adapterDescription) throws AdapterException, ParseException {
    var adapter = getDeclarerSingleton()
        .getAdapter(adapterDescription.getAppId());

    if (adapter.isPresent()) {
      var adapterInstance = adapter.get();

      LOG.info("Start guessing schema for: " + adapterDescription.getAppId());

      // get registered parser of adapter
      var registeredParsers = adapterInstance.declareConfig().getSupportedParsers();

      var extractor = AdapterParameterExtractor.from(adapterDescription, registeredParsers);

      try {
        var schema = adapterInstance
            .onSchemaRequested(extractor, guessSchemaContext);
        LOG.info("Start guessing schema for: " + adapterDescription.getAppId());

        return schema;
      } catch (ParseException e) {
        LOG.error(e.toString());

        String errorClass = "";
        Optional<StackTraceElement> stackTraceElement = Arrays.stream(e.getStackTrace()).findFirst();
        if (stackTraceElement.isPresent()) {
          String[] errorClassLong = stackTraceElement.get().getClassName().split("\\.");
          errorClass = errorClassLong[errorClassLong.length - 1] + ": ";
        }
        throw new ParseException(errorClass + e.getMessage());
      } catch (Exception e) {
        throw new AdapterException(e.getMessage(), e);
      }

    } else {
      throw new AdapterException("Adapter with app id %s was not be found".formatted(adapterDescription.getAppId()));
    }

  }

  public IDeclarersSingleton getDeclarerSingleton() {
    return DeclarersSingleton.getInstance();
  }

}
