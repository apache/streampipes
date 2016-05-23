package de.fzi.cep.sepa.client.container.init;

import de.fzi.cep.sepa.desc.declarer.Declarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProducerDeclarer;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

public class DeclarersSingleton {
    private static DeclarersSingleton instance;

    private List<SemanticEventProcessingAgentDeclarer> epaDeclarers;
    private List<SemanticEventProducerDeclarer> producerDeclarers;
    private List<SemanticEventConsumerDeclarer> consumerDeclarers;


    public DeclarersSingleton() {
        this.epaDeclarers = new ArrayList<>();
        this.producerDeclarers = new ArrayList<>();
        this.consumerDeclarers = new ArrayList<>();
    }

    public static DeclarersSingleton getInstance() {
        if (DeclarersSingleton.instance == null) {
            DeclarersSingleton.instance = new DeclarersSingleton();
        }
        return DeclarersSingleton.instance;
    }

    public void addDeclarers(List<Declarer> allDeclarers) {
        for (Declarer d : allDeclarers) {
            addDeclarer(d);
        }
    }

    public void addDeclarer(Declarer d) {
        if (d instanceof SemanticEventProcessingAgentDeclarer) {
            addEpaDeclarer((SemanticEventProcessingAgentDeclarer) d);
        } else if (d instanceof SemanticEventProducerDeclarer) {
            addProducerDeclarer((SemanticEventProducerDeclarer) d);
        } else if (d instanceof SemanticEventConsumerDeclarer) {
            addConsumerDeclarer((SemanticEventConsumerDeclarer) d);
        }
    }

    //TODO remove this method
    public List<Declarer> getDeclarers() {
        List<Declarer> result = new ArrayList<>();
        result.addAll(epaDeclarers);
        return result;
    }


    public void addEpaDeclarer(SemanticEventProcessingAgentDeclarer epaDeclarer) {
        epaDeclarers.add(epaDeclarer);
    }

    public void addProducerDeclarer(SemanticEventProducerDeclarer sourceDeclarer) {
        producerDeclarers.add(sourceDeclarer);
    }

    public void addConsumerDeclarer(SemanticEventConsumerDeclarer consumerDeclarer) {
        consumerDeclarers.add(consumerDeclarer);
    }

    public List<SemanticEventProcessingAgentDeclarer> getEpaDeclarers() {
        return epaDeclarers;
    }

    public List<SemanticEventProducerDeclarer> getProducerDeclarers() {
        return producerDeclarers;
    }

    public List<SemanticEventConsumerDeclarer> getConsumerDeclarers() {
        return consumerDeclarers;
    }
}
