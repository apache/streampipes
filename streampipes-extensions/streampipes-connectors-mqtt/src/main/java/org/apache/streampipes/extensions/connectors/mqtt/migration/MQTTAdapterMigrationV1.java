package org.apache.streampipes.extensions.connectors.mqtt.migration;

import static org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConnectUtils.getBrokerUrlLabel;

import java.util.Arrays;
import java.util.List;

import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.migration.IAdapterMigrator;
import org.apache.streampipes.extensions.connectors.mqtt.adapter.MqttProtocol;
import org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConnectUtils;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.SlideToggleStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.sdk.StaticProperties;


public class MQTTAdapterMigrationV1 implements IAdapterMigrator {
    
    @Override
    public ModelMigratorConfig config() {
        return new ModelMigratorConfig(
                MqttProtocol.ID,
                SpServiceTagPrefix.ADAPTER,
                0,
                1);

    }

    @Override
  public MigrationResult<AdapterDescription> migrate(AdapterDescription element,
                                                     IStaticPropertyExtractor extractor) throws RuntimeException {

var url = (FreeTextStaticProperty) element.getConfig().get(0);
url.setDescription("Example: tcp://test-server.com:1883 (Protocol required. Port required), with TLS ssl://test-server.com:8883 (Protocol required. Port required)");
element.getConfig().set(0, url);

migrateSecurity((StaticPropertyAlternatives) element.getConfig().get(1));

element.getConfig().add(2, makeTLS());
       return MigrationResult.success(element);
  }

private SlideToggleStaticProperty makeTLS() {
        var tlsAlternative = StaticProperties.toggleAlternative(MqttConnectUtils.getTLS(), false);
        return tlsAlternative;
    }

public void migrateSecurity(StaticPropertyAlternatives securityAlternatives) {
    migrateGroup(securityAlternatives.getAlternatives());
  }

  private void migrateGroup(List<StaticPropertyAlternative> alternatives) {
    alternatives.add(MqttConnectUtils.getAlternativesThree());
    
  }


}
