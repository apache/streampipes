package de.fzi.cep.sepa.implementations.stream.story.main;

import de.fzi.cep.sepa.client.container.init.ContainerModelSubmitter;
import de.fzi.cep.sepa.client.init.DeclarersSingleton;
import de.fzi.cep.sepa.implementations.stream.story.sepas.ActivityDetectionController;


public class StreamStoryInit extends ContainerModelSubmitter {
    //	public static String STREAMSTORY_URL = "http://streamstory.de/";
    //	public static String STREAMSTORY_URL = "http://requestb.in/za5qurza";
    public static String STREAMSTORY_URL = "http://localhost:3000/";

    public void init() {

        DeclarersSingleton.getInstance().setRoute("stream-story");
        DeclarersSingleton.getInstance()
                .add(new ActivityDetectionController());

    }
}
