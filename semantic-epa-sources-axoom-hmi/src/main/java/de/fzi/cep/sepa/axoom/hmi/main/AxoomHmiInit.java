package de.fzi.cep.sepa.axoom.hmi.main;

import de.fzi.cep.sepa.axoom.hmi.sepa.LabelOrder;
import de.fzi.cep.sepa.axoom.hmi.sepa.LabelOrderController;
import de.fzi.cep.sepa.axoom.hmi.sources.AxoomHmiProducer;
import de.fzi.cep.sepa.client.init.DeclarersSingleton;
import de.fzi.cep.sepa.client.standalone.init.StandaloneModelSubmitter;

/**
 * Created by riemer on 16.03.2017.
 */
public class AxoomHmiInit extends StandaloneModelSubmitter {

  public static void main(String[] args) {
    DeclarersSingleton.getInstance()
            .add(new AxoomHmiProducer())
            .add(new LabelOrderController());
    DeclarersSingleton.getInstance().setPort(8070);
    new AxoomHmiInit().init();
  }
}
