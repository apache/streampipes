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

package org.apache.streampipes.extensions.connectors.plc.adapter.migration;

import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.migration.IAdapterMigrator;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.config.AdapterConfigurationProvider;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.config.MetadataOptionGenerator;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.model.staticproperty.CodeInputStaticProperty;
import org.apache.streampipes.model.staticproperty.CollectionStaticProperty;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.OneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.SecretStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;

import org.apache.plc4x.java.api.PlcDriver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Upgrades saved options and tag addresses without changing configured values or alternative selections. */
public class Plc4xAdapterMigrationV2 implements IAdapterMigrator {

  private final String appId;
  private final PlcDriver driver;

  public Plc4xAdapterMigrationV2(PlcDriver driver) {
    this(AdapterConfigurationProvider.ID + driver.getProtocolCode(), driver);
  }

  public Plc4xAdapterMigrationV2(String appId, PlcDriver driver) {
    this.appId = appId;
    this.driver = driver;
  }

  @Override
  public ModelMigratorConfig config() {
    return new ModelMigratorConfig(appId, SpServiceTagPrefix.ADAPTER, 1, 2);
  }

  @Override
  public MigrationResult<AdapterDescription> migrate(AdapterDescription element, IStaticPropertyExtractor extractor) {
    var migrated = new AdapterDescription(element);
    try {
      String oldTransport = "";
      String newTransport = "";
      for (var property : migrated.getConfig()) {
        if (property instanceof OneOfStaticProperty transports
            && Plc4xLabels.SUPPORTED_TRANSPORTS.equals(property.getInternalName())) {
          oldTransport = transports.getOptions().stream().filter(Option::isSelected)
              .map(Option::getName).findFirst().orElseThrow();
          newTransport = "s7".equals(driver.getProtocolCode()) && "tcp".equals(oldTransport) ? "cotp" : oldTransport;
          var options = new ArrayList<Option>();
          for (var code : driver.getMetadata().getSupportedTransportCodes()) {
            if (!"test".equals(code)) {
              var option = new Option(code);
              option.setSelected(code.equals(newTransport));
              options.add(option);
            }
          }
          if (options.stream().noneMatch(Option::isSelected)) {
            return MigrationResult.failure(element, "The configured PLC transport is no longer supported");
          }
          transports.setOptions(options);
        }
      }
      var options = configurationOptions(newTransport);
      migrated.setConfig(migrateProperties(migrated.getConfig(), "", oldTransport, newTransport, options));
      return MigrationResult.success(migrated);
    } catch (IllegalArgumentException e) {
      return MigrationResult.failure(element, "Could not migrate PLC configuration: " + e.getMessage());
    }
  }

  private Map<String, org.apache.plc4x.java.api.metadata.Option> configurationOptions(String transport) {
    var options = new HashMap<String, org.apache.plc4x.java.api.metadata.Option>();
    driver.getMetadata().getProtocolConfigurationOptionMetadata().ifPresent(metadata ->
        metadata.getOptions().forEach(option -> options.put(option.getKey(), option)));
    if (!transport.isEmpty()) {
      driver.getMetadata().getTransportConfigurationOptionMetadata(transport).ifPresent(metadata ->
          metadata.getOptions().forEach(option -> options.put(
              option.getKey().contains(".") ? option.getKey() : transport + "." + option.getKey(), option)));
    }
    return options;
  }

  private List<StaticProperty> migrateProperties(List<StaticProperty> properties,
                                                 String context,
                                                 String oldTransport,
                                                 String newTransport,
                                                 Map<String, org.apache.plc4x.java.api.metadata.Option> options) {
    var result = new ArrayList<StaticProperty>();
    for (var property : properties) {
      var name = property.getInternalName();
      if (property instanceof StaticPropertyAlternatives alternatives) {
        var nestedContext = Plc4xLabels.TRANSPORT_METADATA.equals(name) ? "transport"
            : Plc4xLabels.PROTOCOL_METADATA.equals(name) ? "protocol" : context;
        for (var alternative : alternatives.getAlternatives()) {
          alternative.setStaticProperty(migrateProperties(List.of(alternative.getStaticProperty()),
              nestedContext, oldTransport, newTransport, options).getFirst());
        }
      } else if (property instanceof StaticPropertyGroup group) {
        group.setStaticProperties(migrateProperties(group.getStaticProperties(), context,
            oldTransport, newTransport, options));
      } else if (property instanceof CollectionStaticProperty collection) {
        collection.setMembers(migrateProperties(collection.getMembers(), context,
            oldTransport, newTransport, options));
      } else if (property instanceof CodeInputStaticProperty code && Plc4xLabels.PLC_CODE_BLOCK.equals(name)) {
        code.setValue(Plc4xV1Configuration.code(driver.getProtocolCode(), code.getValue()));
      } else if (property instanceof FreeTextStaticProperty text) {
        if (Plc4xLabels.PLC_IP.equals(name) && text.getValue() != null) {
          text.setValue(Plc4xV1Configuration.host(driver.getProtocolCode(), text.getValue()));
        } else if (!context.isEmpty()) {
          String key = "transport".equals(context) && !name.contains(".") ? oldTransport + "." + name : name;
          String migratedKey = Plc4xV1Configuration.optionName(driver.getProtocolCode(), key);
          String internalName = "transport".equals(context) && migratedKey.startsWith(newTransport + ".")
              ? migratedKey.substring(newTransport.length() + 1) : migratedKey;
          text.setInternalName(internalName);
          text.setLabel(internalName);
          text.setValue(Plc4xV1Configuration.optionValue(key, text.getValue()));
          var option = options.get(migratedKey);
          if (option != null) {
            var updated = new MetadataOptionGenerator().getOptions(List.of(option))[0];
            updated.setInternalName(internalName);
            if (updated instanceof SecretStaticProperty secret) {
              secret.setValue(text.getValue());
              secret.setEncrypted(false);
            } else if (updated instanceof FreeTextStaticProperty value) {
              value.setValue(text.getValue());
            }
            property = updated;
          }
        }
      }
      result.add(property);
    }
    return result;
  }
}
