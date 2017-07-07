package de.fzi.cep.sepa.manager.kpi;

import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.storage.controller.StorageManager;

import java.util.List;
import java.util.Optional;

/**
 * Created by riemer on 04.10.2016.
 */
public class KpiPipelineBuilderUtils {

    public static Optional<SepaDescription> getSepa(String suffix) {
        List<SepaDescription> allSepas = StorageManager.INSTANCE.getStorageAPI().getAllSEPAs();

        return allSepas
                .stream()
                .map(s -> new SepaDescription(s))
                .filter(s -> s.getElementId().endsWith(suffix))
                .findFirst();
    }

    public static Optional<SecDescription> getSec(String suffix) {
        List<SecDescription> allSecs = StorageManager.INSTANCE.getStorageAPI().getAllSECs();

        return allSecs
                .stream()
                .map(s -> new SecDescription(s))
                .filter(s -> s.getElementId().endsWith(suffix))
                .findFirst();
    }
}
