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

package org.apache.streampipes.sinks.brokers.jvm.migrations;

import org.apache.streampipes.extensions.api.extractor.IDataSinkParameterExtractor;
import org.apache.streampipes.extensions.api.migration.IDataSinkMigrator;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.model.staticproperty.CollectionStaticProperty;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.SlideToggleStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sinks.brokers.jvm.rest.RestSink;
import org.apache.streampipes.vocabulary.XSD;

import java.util.List;

public class RestSinkMigrationV1 implements IDataSinkMigrator {

  @Override
  public ModelMigratorConfig config() {
    return new ModelMigratorConfig(
        "org.apache.streampipes.sinks.brokers.jvm.rest",
        SpServiceTagPrefix.DATA_SINK,
        0,
        1
    );
  }

  @Override
  public MigrationResult<DataSinkInvocation> migrate(DataSinkInvocation element,
                                                     IDataSinkParameterExtractor extractor) throws RuntimeException {
    // Extract the URL value from the old configuration
    String urlValue = extractor.singleValueParameter(RestSink.URL_KEY, String.class);
    if (urlValue == null) {
      return MigrationResult.failure(element, "URL property not found in old configuration");
    }

    addRetryDelayKey(element);
    addIsRetryEnabledKey(element);
    addRetryMaxRetriesKey(element);
    addHeaderCollectionKeys(element);

    return MigrationResult.success(element);
  }

  public void addRetryDelayKey(DataSinkInvocation element) {
    var label = Labels.from(
        RestSink.RETRY_DELAY_MS_KEY,
        "Retry Delay (ms)", "Duration in ms to wait for request to "
            + "retry."
    );
    var staticProperty = new FreeTextStaticProperty(
        label.getInternalId(),
        label.getLabel(),
        label.getDescription()
    );
    staticProperty.setRequiredDatatype(XSD.INTEGER);

    element.getStaticProperties().add(staticProperty);
  }

  public void addIsRetryEnabledKey(DataSinkInvocation element) {
    var label = Labels.from(RestSink.IS_RETRY_ENABLED_KEY, "Enable Retry",
        "If enabled, the request will be retried at an interval defined by the retry delay up to max retries.");
    var staticProperty = new SlideToggleStaticProperty(
        label.getInternalId(),
        label.getLabel(),
        label.getDescription(),
        false
    );
    staticProperty.setSelected(false);
    element.getStaticProperties().add(staticProperty);
  }

  public void addRetryMaxRetriesKey(DataSinkInvocation element) {
    var label = Labels.from(RestSink.RETRY_MAX_RETRIES_KEY, "Max Retries",
        "The maximum number of retries allowed for request to retry.");
    var staticProperty = new FreeTextStaticProperty(
        label.getInternalId(),
        label.getLabel(),
        label.getDescription()
    );
    staticProperty.setRequiredDatatype(XSD.INTEGER);
    element.getStaticProperties().add(staticProperty);
  }

  public void addHeaderCollectionKeys(DataSinkInvocation element) {
    StaticPropertyGroup staticPropertyGroup = new StaticPropertyGroup();
    staticPropertyGroup.setLabel("Request Headers");
    staticPropertyGroup.setInternalName(RestSink.HEADER_COLLECTION);
    staticPropertyGroup.setHorizontalRendering(false);

    var headerKeyLabel = StaticProperties.stringFreeTextProperty(
        Labels.from(
            RestSink.HEADER_KEY,
            "Header",
            "Optional custom headers to be included with the REST request."
        )
    );
    headerKeyLabel.setValue("");
    headerKeyLabel.setOptional(true);

    var headerValueLabel = StaticProperties.stringFreeTextProperty(
        Labels.from(
            RestSink.HEADER_VALUE,
            "Header Value",
            "Optional custom headers to be included with the REST "
                + "request."
        )
    );
    headerValueLabel.setValue("");
    headerValueLabel.setOptional(true);
    staticPropertyGroup.getStaticProperties().addAll(
        List.of(
            headerKeyLabel,
            headerValueLabel
        )
    );

    var collection = new CollectionStaticProperty(
        RestSink.HEADER_COLLECTION,
        "Request Headers",
        "Optional custom headers to be included with the REST request.",
        staticPropertyGroup
    );
    element.getStaticProperties().add(collection);
  }
}
