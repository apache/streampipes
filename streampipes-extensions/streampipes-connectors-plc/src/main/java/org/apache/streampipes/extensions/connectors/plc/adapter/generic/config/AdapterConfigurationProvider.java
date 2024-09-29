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
package org.apache.streampipes.extensions.connectors.plc.adapter.generic.config;

import static org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels.PLC_CODE_BLOCK;
import static org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels.PLC_IP;
import static org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels.PLC_POLLING_INTERVAL;
import static org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels.PROTOCOL_METADATA;
import static org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels.SUPPORTED_TRANSPORTS;
import static org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels.TRANSPORT_METADATA;
import static org.apache.streampipes.extensions.connectors.plc.adapter.s7.Plc4xS7Adapter.CODE_TEMPLATE;

import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.GenericPlc4xAdapter;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.assets.PlcAdapterAssetResolver;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.helpers.CodeLanguage;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;

import org.apache.plc4x.java.api.PlcConnectionManager;
import org.apache.plc4x.java.api.PlcDriver;

public class AdapterConfigurationProvider {

  public static final String ID = "org.apache.streampipes.connect.iiot.adapters.plc4x.generic.";

  public IAdapterConfiguration makeConfig(PlcDriver driver, PlcConnectionManager connectionManager) {
    var driverMetadata = driver.getMetadata();
    var appId = getAdapterAppId(driver);
    var adapterBuilder = AdapterConfigurationBuilder
            .create(appId, 1, () -> new GenericPlc4xAdapter(driver, connectionManager)).withLocales(Locales.EN)
            .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
            .withAssetResolver(new PlcAdapterAssetResolver("org.apache.streampipes.connect.iiot.adapters.plc4x.generic",
                    appId, driver))
            .withCategory(AdapterType.Manufacturing).requiredTextParameter(Labels.withId(PLC_IP))
            .requiredIntegerParameter(Labels.withId(PLC_POLLING_INTERVAL), 1000)
            .requiredSingleValueSelection(Labels.withId(SUPPORTED_TRANSPORTS),
                    driverMetadata.getSupportedTransportCodes().stream().map(Option::new).toList())
            .requiredStaticProperty(new MetadataOptionGenerator().makeRuntimeResolvableMetadata(TRANSPORT_METADATA));
    var protocolMetadata = driverMetadata.getProtocolConfigurationOptionMetadata();

    protocolMetadata.ifPresent(optionMetadata -> adapterBuilder
            .requiredStaticProperty(new MetadataOptionGenerator().makeMetadata(PROTOCOL_METADATA, optionMetadata)));
    adapterBuilder.requiredCodeblock(Labels.withId(PLC_CODE_BLOCK), CodeLanguage.None, CODE_TEMPLATE);

    return adapterBuilder.buildConfiguration();
  }

  private String getAdapterAppId(PlcDriver driver) {
    return ID + driver.getProtocolCode();
  }

}
