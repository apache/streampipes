package org.apache.streampipes.extensions.connectors.opcua.migration;

import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.migration.IAdapterMigrator;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Labels;

import java.util.List;

import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.*;

public class OpcUaAdapterMigrationV2 implements IAdapterMigrator {
    @Override
    public ModelMigratorConfig config() {
        return new ModelMigratorConfig(
                "org.apache.streampipes.connect.iiot.adapters.opcua",
                SpServiceTagPrefix.ADAPTER,
                1,
                2);
    }

    @Override
    public MigrationResult<AdapterDescription> migrate(AdapterDescription element, IStaticPropertyExtractor extractor) throws RuntimeException {
        var newConfigs = element.getConfig().stream().map(config->{
            if (isHostOrUrlConfig(config)){
                return modifiedAlternatives();
            } else {
                return config;
            }
        }).toList();

        element.setConfig(newConfigs);
        return MigrationResult.success(element);
    }

    private StaticProperty modifiedAlternatives() {
        return StaticProperties.alternatives(Labels.withId(OPC_HOST_OR_URL),
                Alternatives.from(
                        Labels.withId(OPC_URL),
                        StaticProperties.stringFreeTextProperty(
                                Labels.withId(OPC_SERVER_URL), "opc.tcp://localhost:4840"))
                ,
                Alternatives.from(Labels.withId(OPC_HOST),
                        StaticProperties.group(
                                Labels.withId(HOST_PORT),
                                StaticProperties.stringFreeTextProperty(
                                        Labels.withId(OPC_SERVER_HOST)),
                                StaticProperties.integerFreeTextProperty(
                                        Labels.withId(OPC_SERVER_PORT))
                        )));
    }

    private boolean isHostOrUrlConfig(StaticProperty config){
        return config.getInternalName().equals(OPC_HOST_OR_URL.name());
    }
}
