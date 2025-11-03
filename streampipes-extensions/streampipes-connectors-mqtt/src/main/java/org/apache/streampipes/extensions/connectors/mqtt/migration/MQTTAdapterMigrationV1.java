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
LOG.info ("Start Info Migrate Option");
LOG.info(""+element.getConfig().get(1));
// Migrate Options

migrateSecurity((StaticPropertyAlternatives) element.getConfig().get(1));

LOG.info(element.getConfig().get(1).getLabel());

LOG.info ("Start Info Migrate RLS ");
    element.getConfig().add(2, makeTLS());
       return MigrationResult.success(element);
  }

private SlideToggleStaticProperty makeTLS() {
    LOG.info("GET TLS ALTERNATIVE");
        var tlsAlternative = StaticProperties.toggleAlternative(MqttConnectUtils.getTLS(), false);
        
           LOG.info("TLS ALT", tlsAlternative);
         LOG.info("setSelected" );
        return tlsAlternative;
    }

public void migrateSecurity(StaticPropertyAlternatives securityAlternatives) {
    LOG.info("Migrate Security"+securityAlternatives.getAlternatives());
    migrateGroup(securityAlternatives.getAlternatives());
  }

  private void migrateGroup(List<StaticPropertyAlternative> alternatives) {
    //boolean selected = alternative.getSelected();

    //var securityMechanism = StaticProperties.alternatives(
    //    Labels.withId(MqttConnectUtils.ACCESS_MODE),
    //  Arrays.asList(
    //MqttConnectUtils.getAlternativesOne(),
    //MqttConnectUtils.getAlternativesTwo(),
    //MqttConnectUtils.getAlternativesThree()
//));

    //((StaticPropertyGroup) alternative.getStaticProperty()).getStaticProperties().add(
    //    0,
    //    securityMechanism
    //);
    alternatives.add(MqttConnectUtils.getAlternativesThree());

    
  
    
  }


}
