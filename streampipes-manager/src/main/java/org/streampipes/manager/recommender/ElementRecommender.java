package org.streampipes.manager.recommender;

import com.rits.cloning.Cloner;
import org.streampipes.commons.Utils;
import org.streampipes.commons.exceptions.NoSepaInPipelineException;
import org.streampipes.commons.exceptions.NoSuitableSepasAvailableException;
import org.streampipes.manager.matching.PipelineVerificationHandler;
import org.streampipes.manager.util.PipelineVerificationUtils;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.client.pipeline.Pipeline;
import org.streampipes.model.client.pipeline.PipelineElementRecommendation;
import org.streampipes.model.client.pipeline.PipelineElementRecommendationMessage;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.storage.controller.StorageManager;
import org.apache.commons.lang.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ElementRecommender {

    private Pipeline pipeline;
    private String email;
    private PipelineElementRecommendationMessage recommendationMessage;
    private Cloner cloner;

    public ElementRecommender(String email, Pipeline partialPipeline) {
        this.email = email;
        this.pipeline = partialPipeline;
        this.recommendationMessage = new PipelineElementRecommendationMessage();
        this.cloner = new Cloner();
    }

    public PipelineElementRecommendationMessage findRecommendedElements() throws NoSuitableSepasAvailableException {
        String connectedTo;
        String rootNodeElementId;
        try {
            InvocableStreamPipesEntity sepaElement = getRootNode();
            sepaElement.setConfigured(true);
            rootNodeElementId = sepaElement.getBelongsTo();
            connectedTo = sepaElement.getDOM();
        } catch (NoSepaInPipelineException e) {
            connectedTo = pipeline.getStreams().get(0).getDOM();
            rootNodeElementId = pipeline.getStreams().get(0).getElementId();
        }
        validateSepas(connectedTo);
        validateSecs(connectedTo);

        if (recommendationMessage.getPossibleElements().size() == 0) throw new NoSuitableSepasAvailableException();
        else {
            recommendationMessage
                    .setRecommendedElements(calculateWeights(
                            filterOldElements(StorageManager
                                    .INSTANCE
                                    .getConnectionStorageApi()
                                    .getRecommendedElements(rootNodeElementId))));
            return recommendationMessage;
        }
    }

    private List<PipelineElementRecommendation> filterOldElements(List<PipelineElementRecommendation> recommendedElements) {
        return recommendedElements
                .stream()
                .filter(r -> getAll()
                        .stream()
                        .anyMatch(a -> a.getElementId().equals(r.getElementId())))
                .collect(Collectors.toList());
    }

    private List<PipelineElementRecommendation> calculateWeights(List<PipelineElementRecommendation> recommendedElements) {
        int allConnectionsCount = recommendedElements
                .stream()
                .mapToInt(r -> r.getCount())
                .sum();

        recommendedElements
                .forEach(r -> {
                    r.setWeight(getWeight(r.getCount(), allConnectionsCount));
                    r.setName(getName(r.getElementId()));
                    r.setDescription(getDescription(r.getElementId()));
                });

        return recommendedElements;

    }

    private String getName(String elementId) {
        return filter(elementId).getName();
    }

    private String getDescription(String elementId) {
        return filter(elementId).getDescription();
    }

    private NamedStreamPipesEntity filter(String elementId) {
        List<NamedStreamPipesEntity> allElements = getAll();
        return allElements
                .stream()
                .filter(a -> a.getElementId().equals(elementId))
                .findFirst()
                .get();
    }

    private Float getWeight(Integer count, Integer allConnectionsCount) {
        return ((float) (count)) / allConnectionsCount;
    }

    private void validateSepas(String connectedTo) {
        List<DataProcessorDescription> sepas = getAllSepas();
        for (DataProcessorDescription sepa : sepas) {
            sepa = new DataProcessorDescription(sepa);
            Pipeline tempPipeline = cloner.deepClone(pipeline);
            DataProcessorInvocation newSepa = generateSepa(sepa, connectedTo);
            tempPipeline.getSepas().add(newSepa);
            validateConnection(tempPipeline, sepa);
            tempPipeline.setSepas(new ArrayList<>());
        }
    }

    private void validateSecs(String connectedTo) {
        List<DataSinkDescription> secs = getAllSecs();
        for (DataSinkDescription sec : secs) {
            sec = new DataSinkDescription(sec);
            Pipeline tempPipeline = cloner.deepClone(pipeline);
            DataSinkInvocation newSec = generateSec(sec, connectedTo);
            tempPipeline.getActions().add(newSec);
            validateConnection(tempPipeline, sec);
            tempPipeline.setSepas(new ArrayList<>());
        }
    }

    private void validateConnection(Pipeline tempPipeline, NamedStreamPipesEntity currentElement) {
        try {
            new PipelineVerificationHandler(tempPipeline)
                    .validateConnection()
                    .getPipelineModificationMessage();
            addPossibleElements(currentElement);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private DataProcessorInvocation generateSepa(DataProcessorDescription sepa, String connectedTo) {
        DataProcessorInvocation invocation = new DataProcessorInvocation(sepa);
        invocation.setConnectedTo(Utils.createList(connectedTo));
        invocation.setDOM(RandomStringUtils.randomAlphanumeric(5));
        return invocation;
    }

    private DataSinkInvocation generateSec(DataSinkDescription sec, String connectedTo) {
        DataSinkInvocation invocation = new DataSinkInvocation(sec);
        invocation.setConnectedTo(Utils.createList(connectedTo));
        invocation.setDOM(RandomStringUtils.randomAlphanumeric(5));
        return invocation;
    }

    private void addPossibleElements(NamedStreamPipesEntity sepa) {
        recommendationMessage.addPossibleElement(new PipelineElementRecommendation(sepa.getElementId().toString(), sepa.getName(), sepa.getDescription()));
    }

    private List<DataProcessorDescription> getAllSepas() {
        List<String> userObjects = StorageManager.INSTANCE.getUserService().getOwnSepaUris(email);
        return StorageManager
                .INSTANCE
                .getStorageAPI()
                .getAllSEPAs()
                .stream()
                .filter(e -> userObjects.stream().anyMatch(u -> u.equals(e.getElementId())))
                .collect(Collectors.toList());
    }

    private List<DataSinkDescription> getAllSecs() {
        List<String> userObjects = StorageManager.INSTANCE.getUserService().getOwnActionUris(email);
        return StorageManager
                .INSTANCE
                .getStorageAPI()
                .getAllSECs()
                .stream()
                .filter(e -> userObjects.stream().anyMatch(u -> u.equals(e.getElementId())))
            .collect(Collectors.toList());
    }

    private List<NamedStreamPipesEntity> getAll() {
        List<NamedStreamPipesEntity> allElements = new ArrayList<>();
        allElements.addAll(getAllSepas());
        allElements.addAll(getAllSecs());
        return allElements;
    }

    private InvocableStreamPipesEntity getRootNode() throws NoSepaInPipelineException {
        return PipelineVerificationUtils.getRootNode(pipeline);
    }
}
