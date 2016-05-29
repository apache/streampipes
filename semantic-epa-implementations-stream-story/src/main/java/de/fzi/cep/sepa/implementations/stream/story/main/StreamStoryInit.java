package de.fzi.cep.sepa.implementations.stream.story.main;

import de.fzi.cep.sepa.actions.samples.table.TableViewController;
import de.fzi.cep.sepa.client.container.init.ContainerModelSubmitter;
import de.fzi.cep.sepa.client.declarer.Declarer;
import de.fzi.cep.sepa.client.init.DeclarersSingleton;
import de.fzi.cep.sepa.client.standalone.init.StandaloneModelSubmitter;
import de.fzi.cep.sepa.implementations.stream.story.activitydetection.ActivityDetectionController;
import de.fzi.cep.sepa.sources.mhwirth.ddm.DDMProducer;

import javax.servlet.ServletContextEvent;

public class StreamStoryInit extends ContainerModelSubmitter {
//public class StreamStoryInit extends StandaloneModelSubmitter {

    public void init() {

        DeclarersSingleton.getInstance().setRoute("stream-story");
        DeclarersSingleton.getInstance()
                .add(new ActivityDetectionController())
                .add(new DDMProducer())
                .add(new TableViewController());

    }

//    public static void main(String[] args) {
//        DeclarersSingleton.getInstance().setPort(8081);
//        DeclarersSingleton.getInstance()
//                .add(new ActivityDetectionController())
//                .add(new DDMProducer())
//                .add(new TableViewController());
//
//        new StreamStoryInit().init();
//    }


}
