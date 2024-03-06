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

package org.apache.streampipes.extensions.connectors.plc.adapter.migration.config;

import org.apache.streampipes.extensions.connectors.plc.adapter.modbus.Plc4xModbusAdapter;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.utils.Assets;

import static org.apache.streampipes.extensions.connectors.plc.adapter.modbus.Plc4xModbusAdapter.ID;
import static org.apache.streampipes.extensions.connectors.plc.adapter.modbus.Plc4xModbusAdapter.PLC_IP;
import static org.apache.streampipes.extensions.connectors.plc.adapter.modbus.Plc4xModbusAdapter.PLC_NODES;
import static org.apache.streampipes.extensions.connectors.plc.adapter.modbus.Plc4xModbusAdapter.PLC_NODE_ADDRESS;
import static org.apache.streampipes.extensions.connectors.plc.adapter.modbus.Plc4xModbusAdapter.PLC_NODE_ID;
import static org.apache.streampipes.extensions.connectors.plc.adapter.modbus.Plc4xModbusAdapter.PLC_NODE_RUNTIME_NAME;
import static org.apache.streampipes.extensions.connectors.plc.adapter.modbus.Plc4xModbusAdapter.PLC_NODE_TYPE;
import static org.apache.streampipes.extensions.connectors.plc.adapter.modbus.Plc4xModbusAdapter.PLC_PORT;

public class Plc4xModbusAdapterVersionedConfig {

  public static AdapterDescription getPlc4xModbusAdapterDescriptionV0() {
    return AdapterConfigurationBuilder.create(ID, 0, Plc4xModbusAdapter::new)
                                      .withLocales(Locales.EN)
                                      .withAssets(Assets.DOCUMENTATION, Assets.ICON)
                                      .withCategory(AdapterType.Manufacturing)
                                      .requiredTextParameter(Labels.withId(PLC_IP))
                                      .requiredTextParameter(Labels.withId(PLC_PORT))
                                      .requiredTextParameter(Labels.withId(PLC_NODE_ID))
                                      .requiredCollection(
                                          Labels.withId(PLC_NODES),
                                          StaticProperties.stringFreeTextProperty(Labels.withId(PLC_NODE_RUNTIME_NAME)),
                                          StaticProperties.integerFreeTextProperty(Labels.withId(PLC_NODE_ADDRESS)),
                                          StaticProperties.singleValueSelection(
                                              Labels.withId(PLC_NODE_TYPE),
                                              Options.from("DiscreteInput", "Coil", "InputRegister", "HoldingRegister")
                                          )
                                      )
                                      .buildConfiguration()
                                      .getAdapterDescription();
  }

}
