package de.fzi.cep.sepa.manager.recommender;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import org.apache.commons.lang.RandomStringUtils;

import com.rits.cloning.Cloner;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.commons.exceptions.NoMatchingFormatException;
import de.fzi.cep.sepa.commons.exceptions.NoMatchingProtocolException;
import de.fzi.cep.sepa.commons.exceptions.NoMatchingSchemaException;
import de.fzi.cep.sepa.commons.exceptions.NoSepaInPipelineException;
import de.fzi.cep.sepa.commons.exceptions.NoSuitableSepasAvailableException;
import de.fzi.cep.sepa.manager.matching.PipelineVerificationHandler;
import de.fzi.cep.sepa.manager.util.PipelineVerificationUtils;
import de.fzi.cep.sepa.messages.ElementRecommendation;
import de.fzi.cep.sepa.messages.RecommendationMessage;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.storage.controller.StorageManager;

public class ElementRecommender {

    private Pipeline pipeline;
    private RecommendationMessage recommendationMessage;
    private Cloner cloner;

    public ElementRecommender(Pipeline partialPipeline) {
        this.pipeline = partialPipeline;
        this.recommendationMessage = new RecommendationMessage();
        this.cloner = new Cloner();
    }

    public RecommendationMessage findRecommendedElements() throws NoSuitableSepasAvailableException {
        String connectedTo;
        String rootNodeElementId;
        try {
            InvocableSEPAElement sepaElement = getRootNode();
            rootNodeElementId = sepaElement.getBelongsTo();
            connectedTo = sepaElement.getDOM();
        } catch (NoSepaInPipelineException e) {
            connectedTo = pipeline.getStreams().get(0).getDOM();
            rootNodeElementId = pipeline.getStreams().get(0).getElementId();
        }
        List<SepaDescription> sepas = getAllSepas();
        for (SepaDescription sepa : sepas) {
            try {
                sepa = new SepaDescription(sepa);
                Pipeline tempPipeline = cloner.deepClone(pipeline);
                SepaInvocation newSepa = generateSepaClient(sepa, connectedTo);
                tempPipeline.getSepas().add(newSepa);
                new PipelineVerificationHandler(tempPipeline, true)
                        .validateConnection()
                        .getPipelineModificationMessage();
                addPossibleElements(sepa);
                tempPipeline.setSepas(new ArrayList<>());
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }

        if (recommendationMessage.getPossibleElements().size() == 0) throw new NoSuitableSepasAvailableException();
        else {
            recommendationMessage.setRecommendedElements(StorageManager.INSTANCE.getConnectionStorageApi().getRecommendedElements(rootNodeElementId));
            return recommendationMessage;
        }
    }

    private SepaInvocation generateSepaClient(SepaDescription sepa, String connectedTo) {
        SepaInvocation invocation = new SepaInvocation(sepa);
        invocation.setConnectedTo(Utils.createList(connectedTo));
        invocation.setDOM(RandomStringUtils.randomAlphanumeric(5));
        return invocation;
    }

    private void addPossibleElements(SepaDescription sepa) {
        recommendationMessage.addPossibleElement(new ElementRecommendation(sepa.getElementId().toString(), sepa.getName(), sepa.getDescription()));
    }

    private List<SepaDescription> getAllSepas() {
        return StorageManager.INSTANCE.getStorageAPI().getAllSEPAs();
    }

    private InvocableSEPAElement getRootNode() throws NoSepaInPipelineException {
        return PipelineVerificationUtils.getRootNode(pipeline);
    }
}
