package de.fzi.cep.sepa.manager.recommender;

import com.rits.cloning.Cloner;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.commons.exceptions.NoSepaInPipelineException;
import de.fzi.cep.sepa.commons.exceptions.NoSuitableSepasAvailableException;
import de.fzi.cep.sepa.manager.matching.PipelineVerificationHandler;
import de.fzi.cep.sepa.manager.util.PipelineVerificationUtils;
import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.client.pipeline.Pipeline;
import de.fzi.cep.sepa.model.client.pipeline.PipelineElementRecommendation;
import de.fzi.cep.sepa.model.client.pipeline.PipelineElementRecommendationMessage;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.storage.controller.StorageManager;
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
            InvocableSEPAElement sepaElement = getRootNode();
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

    private NamedSEPAElement filter(String elementId) {
        List<NamedSEPAElement> allElements = getAll();
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
        List<SepaDescription> sepas = getAllSepas();
        for (SepaDescription sepa : sepas) {
            sepa = new SepaDescription(sepa);
            Pipeline tempPipeline = cloner.deepClone(pipeline);
            SepaInvocation newSepa = generateSepa(sepa, connectedTo);
            tempPipeline.getSepas().add(newSepa);
            validateConnection(tempPipeline, sepa);
            tempPipeline.setSepas(new ArrayList<>());
        }
    }

    private void validateSecs(String connectedTo) {
        List<SecDescription> secs = getAllSecs();
        for (SecDescription sec : secs) {
            sec = new SecDescription(sec);
            Pipeline tempPipeline = cloner.deepClone(pipeline);
            SecInvocation newSec = generateSec(sec, connectedTo);
            tempPipeline.getActions().add(newSec);
            validateConnection(tempPipeline, sec);
            tempPipeline.setSepas(new ArrayList<>());
        }
    }

    private void validateConnection(Pipeline tempPipeline, NamedSEPAElement currentElement) {
        try {
            new PipelineVerificationHandler(tempPipeline)
                    .validateConnection()
                    .getPipelineModificationMessage();
            addPossibleElements(currentElement);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private SepaInvocation generateSepa(SepaDescription sepa, String connectedTo) {
        SepaInvocation invocation = new SepaInvocation(sepa);
        invocation.setConnectedTo(Utils.createList(connectedTo));
        invocation.setDOM(RandomStringUtils.randomAlphanumeric(5));
        return invocation;
    }

    private SecInvocation generateSec(SecDescription sec, String connectedTo) {
        SecInvocation invocation = new SecInvocation(sec);
        invocation.setConnectedTo(Utils.createList(connectedTo));
        invocation.setDOM(RandomStringUtils.randomAlphanumeric(5));
        return invocation;
    }

    private void addPossibleElements(NamedSEPAElement sepa) {
        recommendationMessage.addPossibleElement(new PipelineElementRecommendation(sepa.getElementId().toString(), sepa.getName(), sepa.getDescription()));
    }

    private List<SepaDescription> getAllSepas() {
        List<String> userObjects = StorageManager.INSTANCE.getUserService().getOwnSepaUris(email);
        return StorageManager
                .INSTANCE
                .getStorageAPI()
                .getAllSEPAs()
                .stream()
                .filter(e -> userObjects.stream().anyMatch(u -> u.equals(e.getElementId())))
                .collect(Collectors.toList());
    }

    private List<SecDescription> getAllSecs() {
        List<String> userObjects = StorageManager.INSTANCE.getUserService().getOwnActionUris(email);
        return StorageManager
                .INSTANCE
                .getStorageAPI()
                .getAllSECs()
                .stream()
                .filter(e -> userObjects.stream().anyMatch(u -> u.equals(e.getElementId())))
            .collect(Collectors.toList());
    }

    private List<NamedSEPAElement> getAll() {
        List<NamedSEPAElement> allElements = new ArrayList<>();
        allElements.addAll(getAllSepas());
        allElements.addAll(getAllSecs());
        return allElements;
    }

    private InvocableSEPAElement getRootNode() throws NoSepaInPipelineException {
        return PipelineVerificationUtils.getRootNode(pipeline);
    }
}
