package de.fzi.cep.sepa.tutorial;

import de.fzi.cep.sepa.client.init.DeclarersSingleton;
import de.fzi.cep.sepa.client.standalone.init.StandaloneModelSubmitter;

/**
 * Created by riemer on 12.03.2017.
 */
public class Main extends StandaloneModelSubmitter {

  public static void main(String[] args) {

    DeclarersSingleton.getInstance().add(new VehicleSource());

    DeclarersSingleton.getInstance().setPort(8002);
    new Main().init();

  }

}
