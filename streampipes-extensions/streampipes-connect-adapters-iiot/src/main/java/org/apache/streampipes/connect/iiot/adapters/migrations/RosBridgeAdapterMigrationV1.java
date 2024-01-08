package org.apache.streampipes.connect.iiot.adapters.migrations;

import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.migration.IAdapterMigrator;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.sdk.helpers.Label;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.vocabulary.XSD;

import static org.apache.streampipes.connect.iiot.adapters.ros.RosBridgeAdapter.ROS_PORT_KEY;

public class RosBridgeAdapterMigrationV1 implements IAdapterMigrator {

    @Override
    public ModelMigratorConfig config() {
        return new ModelMigratorConfig(
                "org.apache.streampipes.connect.iiot.adapters.ros",
                SpServiceTagPrefix.ADAPTER,
                0,
                1);
    }

    @Override
    public MigrationResult<AdapterDescription> migrate(AdapterDescription element, IStaticPropertyExtractor extractor) throws RuntimeException {
        var newConfigs = element.getConfig().stream().map(config->{
            if (isPortConfig(config)){
                Label label = Labels.withId(ROS_PORT_KEY);
                return new FreeTextStaticProperty(label.getInternalId(),
                        label.getLabel(),
                        label.getDescription(),
                        XSD.INTEGER);
            } else {
                return config;
            }
        }).toList();
        element.setConfig(newConfigs);

        return MigrationResult.success(element);
    }

    private boolean isPortConfig(StaticProperty config) {
        return config.getInternalName().equals(ROS_PORT_KEY);
    }
}