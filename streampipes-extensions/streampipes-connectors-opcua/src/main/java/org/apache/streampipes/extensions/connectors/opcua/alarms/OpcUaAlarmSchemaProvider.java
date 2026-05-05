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

package org.apache.streampipes.extensions.connectors.opcua.alarms;

import org.apache.streampipes.client.api.IStreamPipesClient;
import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.extensions.connectors.opcua.client.OpcUaClientProvider;
import org.apache.streampipes.extensions.connectors.opcua.config.SpOpcUaConfigExtractor;
import org.apache.streampipes.model.connect.guess.SampleData;
import org.apache.streampipes.sdk.builder.adapter.SampleDataBuilder;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class OpcUaAlarmSchemaProvider {

  private static final long PREVIEW_TIMEOUT_SECONDS = 10L;

  SampleData getSampleData(OpcUaClientProvider clientProvider,
                           IAdapterParameterExtractor extractor,
                           IStreamPipesClient streamPipesClient) throws AdapterException {
    var opcUaConfig = SpOpcUaConfigExtractor.extractAlarmAdapterConfig(
        extractor.getStaticPropertyExtractor(),
        streamPipesClient,
        extractor.getAdapterDescription().getElementId()
    );

    try {
      var connectedClient = clientProvider.getClient(opcUaConfig);
      var queue = new LinkedBlockingQueue<Map<String, Object>>(1);
      var subscriber = new OpcUaAlarmEventSubscriber(connectedClient, opcUaConfig, queue::offer);

      try (subscriber) {
        subscriber.start();

        var sampleEvent = queue.poll(PREVIEW_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        if (sampleEvent == null) {
          throw new AdapterException(
              "Connected successfully, but no OPC UA events were received during the preview window."
          );
        }

        return SampleDataBuilder.create()
            .sample(sampleEvent)
            .build();
      } finally {
        clientProvider.releaseClient(opcUaConfig);
      }
    } catch (AdapterException e) {
      throw e;
    } catch (Exception e) {
      throw new AdapterException("Could not read OPC UA event preview data", e);
    }
  }
}
