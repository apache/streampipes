package de.fzi.cep.sepa.implementations.stream.story.main;

import de.fzi.cep.sepa.actions.samples.table.TableViewController;
import de.fzi.cep.sepa.client.container.init.EmbeddedModelSubmitter;
import de.fzi.cep.sepa.client.init.DeclarersSingleton;
import de.fzi.cep.sepa.client.standalone.init.StandaloneModelSubmitter;
import de.fzi.cep.sepa.implementations.stream.story.activitydetection.ActivityDetectionController;
import de.fzi.cep.sepa.sources.mhwirth.ddm.DDMProducer;

//public class StreamStoryInit extends EmbeddedModelSubmitter {
public class StreamStoryInit extends StandaloneModelSubmitter {

//    public void init() {
//        DeclarersSingleton.getInstance()
//                .add(new ActivityDetectionController())
//                .add(new DDMProducer())
//                .add(new TableViewController());
//    }

    public static void main(String[] args) {
        DeclarersSingleton.getInstance()
                .add(new ActivityDetectionController())
                .add(new DDMProducer())
                .add(new TableViewController());

        new StreamStoryInit().init();
    }
}
