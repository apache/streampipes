package org.apache.streampipes.extensions.connectors.mqtt.migration;

import static org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConnectUtils.getBrokerUrlLabel;

import java.util.Arrays;

import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.migration.IAdapterMigrator;
import org.apache.streampipes.extensions.connectors.mqtt.adapter.MqttProtocol;
import org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConnectUtils;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.helpers.Labels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MQTTAdapterMigrationV1 implements IAdapterMigrator {
    
        private static final Logger LOG = LoggerFactory.getLogger(MQTTAdapterMigrationV1.class);
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
LOG.info ("Start Migrating Adapter MWTT " + element.getConfig());
LOG.info ("Start Info Migrate Writing ");
//Migrate Writing 
var url = (FreeTextStaticProperty) element.getConfig().get(0);
LOG.info (""+url.getLabel());
url.setDescription("Example: tcp://test-server.com:1883 (Protocol required. Port required), with TLS ssl://test-server.com:8883 (Protocol required. Port required)");
LOG.info (""+url.getDescription());
element.getConfig().set(0, url);
  return MigrationResult.success(element);
/**
LOG.info ("Start Info Migrate Option");
// Migrate Options

migrateSecurity((StaticPropertyAlternatives) element.getConfig().get(2));

LOG.info ("Start Info Migrate RLS ");
    element.getConfig().add(3, makeTLS());
       return MigrationResult.success(element);*/
  }

private StaticPropertyAlternatives makeTLS() {

        var tlsAlternative = StaticProperties.alternatives(MqttConnectUtils.getTLS());
        tlsAlternative.getAlternatives().get(0).setSelected(false);
        return tlsAlternative;
    }

public void migrateSecurity(StaticPropertyAlternatives securityAlternatives) {
    migrateGroup(securityAlternatives.getAlternatives().get(2));
  }

  private void migrateGroup(StaticPropertyAlternative alternative) {
    boolean selected = alternative.getSelected();

    var securityMechanism = StaticProperties.alternatives(
        Labels.withId(MqttConnectUtils.ACCESS_MODE),
      Arrays.asList(
    MqttConnectUtils.getAlternativesOne(),
    MqttConnectUtils.getAlternativesTwo(),
    MqttConnectUtils.getAlternativesThree()
));

    ((StaticPropertyGroup) alternative.getStaticProperty()).getStaticProperties().add(
        0,
        securityMechanism
    );
  
    
  }


}
