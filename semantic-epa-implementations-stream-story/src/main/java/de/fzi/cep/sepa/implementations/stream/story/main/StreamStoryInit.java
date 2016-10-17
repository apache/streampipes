package de.fzi.cep.sepa.implementations.stream.story.main;

import de.fzi.cep.sepa.client.container.init.ContainerModelSubmitter;
import de.fzi.cep.sepa.client.init.DeclarersSingleton;
import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.implementations.stream.story.sepas.ActivityDetectionController;
import de.fzi.cep.sepa.implementations.stream.story.sepas.PredictionController;


public class StreamStoryInit extends ContainerModelSubmitter {
    //	public static String STREAMSTORY_URL = "http://streamstory.de/";
    //	public static String STREAMSTORY_URL = "http://requestb.in/za5qurza";
    public static String STREAMSTORY_URL = ClientConfiguration.INSTANCE.getStreamStoryUrl();
//    public static String STREAMSTORY_URL = "http://motorka.ijs.si/lisbon/";

    public void init() {

        DeclarersSingleton.getInstance().setRoute("stream-story");
        DeclarersSingleton.getInstance()
                .add(new PredictionController())
                .add(new ActivityDetectionController());

    }
}
