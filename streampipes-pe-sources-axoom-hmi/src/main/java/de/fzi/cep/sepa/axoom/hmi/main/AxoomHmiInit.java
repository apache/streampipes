package de.fzi.cep.sepa.axoom.hmi.main;

import de.fzi.cep.sepa.axoom.hmi.sources.AxoomIoTCafeProducer;
import de.fzi.cep.sepa.axoom.hmi.sources.AxoomIotHubProducer;
import de.fzi.cep.sepa.client.init.DeclarersSingleton;
import de.fzi.cep.sepa.client.standalone.init.StandaloneModelSubmitter;

/**
 * Created by riemer on 16.03.2017.
 */
public class AxoomHmiInit extends StandaloneModelSubmitter {

  public static void main(String[] args) {
    DeclarersSingleton.getInstance()
            .add(new AxoomIoTCafeProducer())
            .add(new AxoomIotHubProducer());
    DeclarersSingleton.getInstance().setPort(8070);
    new AxoomHmiInit().init();
  }
}
