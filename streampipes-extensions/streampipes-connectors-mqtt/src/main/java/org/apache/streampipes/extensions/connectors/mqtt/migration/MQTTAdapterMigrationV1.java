package org.apache.streampipes.extensions.connectors.mqtt.migration;

import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.migration.IAdapterMigrator;
import org.apache.streampipes.extensions.connectors.mqtt.adapter.MqttProtocol;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.sdk.StaticProperties;

import static org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConnectUtils.getTLS;

public class MQTTAdapterMigrationV1 implements IAdapterMigrator {

  @Override
  public ModelMigratorConfig config() {
    return new ModelMigratorConfig(
        MqttProtocol.ID,
        SpServiceTagPrefix.ADAPTER,
        0,
        1
    );

    
  }

    @Override
  public MigrationResult<AdapterDescription> migrate(AdapterDescription element,
                                                     IStaticPropertyExtractor extractor) throws RuntimeException {

    element.getConfig().add(makeTLS());
    //.removeIf(c -> c.getInternalName().equals(OldNamespaceIndexKey));
  

    return MigrationResult.success(element);
  }

    private StaticPropertyAlternatives makeTLS() {
        //TODO Only add the last option 
    /**var consumerGroupAlternatives = StaticProperties.alternatives(
        MqttConnectUtils.getAlternativesOne(),
        MqttConnectUtils.getAlternativesTwo(),
        MqttConnectUtils.getAlternativesThree()

    );*/

   
    var tlsAlternative = StaticProperties.alternatives(getTLS());  
    tlsAlternative.getAlternatives().get(0).setSelected(false);
    return  tlsAlternative;
    }
    
}
