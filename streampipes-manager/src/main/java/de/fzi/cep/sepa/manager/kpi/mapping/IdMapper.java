package de.fzi.cep.sepa.manager.kpi.mapping;

import de.fzi.cep.sepa.manager.kpi.context.ContextModel;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.storage.controller.StorageManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by riemer on 03.10.2016.
 */
public class IdMapper {

    private ContextModel contextModel;
    private List<Mapping> mappings;

    public IdMapper(ContextModel contextModel) {
        this.contextModel = contextModel;
        this.mappings = mappings();
    }

    public EventStream getEventStream(String contextModelId) {
        String id = findMapping(contextModelId);
        System.out.println(id);

        List<EventStream> allStreams = new ArrayList<>();

        StorageManager.INSTANCE.getStorageAPI().getAllSEPs().forEach(sep -> allStreams.addAll(sep.getEventStreams()));

        return new EventStream(allStreams.stream()
                .filter(stream -> stream.getUri().endsWith(id)).findFirst().get());
    }

    public EventProperty getEventProperty(EventStream stream, String contextModelPropertyId) {
        Optional<EventProperty> propertyOpt = stream
                .getEventSchema()
                .getEventProperties()
                .stream()
                .filter(p -> p.getRuntimeName().equals(contextModelPropertyId))
                .findFirst();

        return propertyOpt.get();
    }

    private String findMapping(String contextModelId) {
        return mappings
                .stream()
                .filter(m -> m.getContextModelName().equals(contextModelId))
                .findFirst()
                .get()
                .getStreamPipesName();
    }

    private List<Mapping> mappings() {
        List<Mapping> mappings = new ArrayList<>();

        // sensor mappings hella
        mappings.add(new Mapping("dustParticle", "source-environmental/dust"));
        mappings.add(new Mapping("machinePlan", ""));
        mappings.add(new Mapping("materialChange", "source-human/rawMaterialChange"));
        mappings.add(new Mapping("materialCertificate", "source-human/rawMaterialCertificate"));
        mappings.add(new Mapping("montrac", "source-montrac/montrac"));
        mappings.add(new Mapping("materialMovement", ""));
        mappings.add(new Mapping("moulding", "source-moulding/moulding"));
        mappings.add(new Mapping("productionPlan", "source-human/productionPlan"));
        mappings.add(new Mapping("visualInspection", "source-visual/scrap"));


        // sensor mapping mhwirth
        mappings.add(new Mapping("1000693", "source-ddm/drilling-rpm"));
        mappings.add(new Mapping("1000700", "source-ddm/torque"));
        mappings.add(new Mapping("1002311", "source-ddm/hookLoad"));
        mappings.add(new Mapping("1000695", "source-ddm/gearLubeTemp"));
        mappings.add(new Mapping("1000692", "source-ddm/gearboxPressure"));
        mappings.add(new Mapping("1000696", "source-ddm/swivelTemperature"));
        return mappings;
    }
}
