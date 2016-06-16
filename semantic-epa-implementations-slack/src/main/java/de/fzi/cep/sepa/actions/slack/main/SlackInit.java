package de.fzi.cep.sepa.actions.slack.main;

import de.fzi.cep.sepa.actions.slack.sec.SlackNotificationController;
import de.fzi.cep.sepa.actions.slack.sep.SlackProducer;
import de.fzi.cep.sepa.client.container.init.ContainerModelSubmitter;
import de.fzi.cep.sepa.client.init.DeclarersSingleton;

public class SlackInit extends ContainerModelSubmitter {
    @Override
    public void init() {
        DeclarersSingleton.getInstance().setRoute("slack");
        DeclarersSingleton.getInstance()
                .add(new SlackNotificationController())
                .add(new SlackProducer());
    }
}
