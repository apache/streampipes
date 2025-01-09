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

package org.apache.streampipes.sinks.notifications.jvm.migrations;

import org.apache.streampipes.extensions.api.extractor.IDataSinkParameterExtractor;
import org.apache.streampipes.extensions.api.migration.IDataSinkMigrator;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.vocabulary.XSD;

import static org.apache.streampipes.wrapper.standalone.StreamPipesNotificationSink.DEFAULT_WAITING_TIME_MINUTES;
import static org.apache.streampipes.wrapper.standalone.StreamPipesNotificationSink.KEY_SILENT_PERIOD;

public interface INotificationDataSinkMigrator extends IDataSinkMigrator  {
  @Override
  default MigrationResult<DataSinkInvocation> migrate(DataSinkInvocation element,
                                                      IDataSinkParameterExtractor extractor) throws RuntimeException {
    if (!isSilentPeriodPresent(element)) {
      var fsp = new FreeTextStaticProperty(KEY_SILENT_PERIOD,
          "Silent Period [min]",
          "The minimum number of minutes between two consecutive notifications that are sent",
          XSD.INTEGER);
      fsp.setValue(String.valueOf(DEFAULT_WAITING_TIME_MINUTES));
      element.getStaticProperties().add(fsp);
    }

    return MigrationResult.success(element);
  }

  default boolean isSilentPeriodPresent(DataSinkInvocation element) {
    return element.getStaticProperties().stream().anyMatch(e -> e.getInternalName().equals(KEY_SILENT_PERIOD));
  }
}
