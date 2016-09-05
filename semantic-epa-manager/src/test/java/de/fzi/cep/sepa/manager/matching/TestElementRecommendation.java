package de.fzi.cep.sepa.manager.matching;

import de.fzi.cep.sepa.commons.exceptions.NoSuitableSepasAvailableException;
import de.fzi.cep.sepa.manager.recommender.ElementRecommender;
import de.fzi.cep.sepa.messages.RecommendationMessage;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.sources.samples.random.RandomDataProducer;
import de.fzi.cep.sepa.sources.samples.random.RandomNumberStreamJson;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by riemer on 31.08.2016.
 */
public class TestElementRecommendation {

    @Test
    public void testSuitableElements() {
        Pipeline pipeline = new Pipeline();

        RandomDataProducer producer = new RandomDataProducer();
        EventStream stream = new RandomNumberStreamJson().declareModel(producer.declareModel());
        stream.setDOM("A");
        pipeline.setStreams(Arrays.asList(new EventStream(stream)));
        pipeline.setSepas(Arrays.asList());

        RecommendationMessage message = null;
        try {
            message = new ElementRecommender(pipeline).findRecommendedElements();
        } catch (NoSuitableSepasAvailableException e) {
            e.printStackTrace();
        }
        assertTrue(message.getPossibleElements().size() > 0);
        System.out.println(message.getPossibleElements().size());
    }
}
