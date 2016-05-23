package de.fzi.cep.sepa.runtime.activity.detection.main;

import de.fzi.cep.sepa.client.container.init.EmbeddedModelSubmitter;
import de.fzi.cep.sepa.desc.declarer.Declarer;
import de.fzi.cep.sepa.sources.mhwirth.ddm.DDMProducer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Init extends EmbeddedModelSubmitter {

    @Override
    public List<Declarer> addDeclarers() {
        return Arrays.asList(new ActivityDetectionController(), new DDMProducer());
    }
}
