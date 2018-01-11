package org.streampipes.pe.axoom.hmi.main;

import org.streampipes.pe.axoom.hmi.config.SourceConfig;
import org.streampipes.pe.axoom.hmi.sources.AxoomIoTCafeProducer;
import org.streampipes.pe.axoom.hmi.sources.AxoomIotHubProducer;
import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.container.standalone.init.StandaloneModelSubmitter;

public class AxoomHmiInit extends StandaloneModelSubmitter {

  public static void main(String[] args) {
    DeclarersSingleton.getInstance()
            .add(new AxoomIoTCafeProducer())
            .add(new AxoomIotHubProducer());

    DeclarersSingleton.getInstance().setPort(SourceConfig.INSTANCE.getPort());
    DeclarersSingleton.getInstance().setHostName(SourceConfig.INSTANCE.getHost());

    new AxoomHmiInit().init(SourceConfig.INSTANCE);
  }
}
