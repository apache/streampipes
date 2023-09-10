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

package org.apache.streampipes.manager.matching;

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.configuration.SpDataFormat;
import org.apache.streampipes.model.grounding.TransportFormat;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.vocabulary.MessageFormat;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FormatSelector extends GroundingSelector {

  public FormatSelector(NamedStreamPipesEntity source, Set<InvocableStreamPipesEntity> targets) {
    super(source, targets);
  }

  public TransportFormat getTransportFormat() {

    if (source instanceof SpDataStream) {
      return ((SpDataStream) source)
          .getEventGrounding()
          .getTransportFormats()
          .get(0);
    } else {
      List<SpDataFormat> prioritizedFormats =
          StorageDispatcher
              .INSTANCE
              .getNoSqlStore()
              .getSpCoreConfigurationStorage()
              .get()
              .getMessagingSettings()
              .getPrioritizedFormats();

      List<SpDataFormat> supportedFormats = prioritizedFormats
          .stream()
          .filter(pf -> supportsFormat(pf.getMessageFormat())).toList();

      if (supportedFormats.size() > 0) {
        return new TransportFormat(supportedFormats.get(0).getMessageFormat());
      } else {
        return new TransportFormat(MessageFormat.JSON);
      }
    }
  }

  public boolean supportsFormat(String format) {
    List<InvocableStreamPipesEntity> elements = buildInvocables();
    return elements
        .stream()
        .allMatch(e -> e
            .getSupportedGrounding()
            .getTransportFormats()
            .stream()
            .anyMatch(s -> rdfTypesAsString(s.getRdfType()).contains(format)));
  }

  private List<String> rdfTypesAsString(List<URI> uri) {
    return uri.stream().map(URI::toString).collect(Collectors.toList());
  }
}
