package de.fzi.cep.sepa.implementations.stream.story.main;

import de.fzi.cep.sepa.actions.samples.table.TableViewController;
import de.fzi.cep.sepa.client.container.init.DeclarersSingleton;
import de.fzi.cep.sepa.client.container.init.EmbeddedModelSubmitter;
import de.fzi.cep.sepa.desc.declarer.Declarer;
import de.fzi.cep.sepa.implementations.stream.story.activitydetection.ActivityDetectionController;
import de.fzi.cep.sepa.sources.mhwirth.ddm.DDMProducer;

import java.util.Arrays;
import java.util.List;

public class StreamStoryInit extends EmbeddedModelSubmitter {

    @Override
    public void init() {
        DeclarersSingleton.getInstance()
                .add(new ActivityDetectionController())
                .add(new DDMProducer())
                .add(new TableViewController());
    }
}
