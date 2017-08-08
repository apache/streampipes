package org.streampipes.container.init;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.container.declarer.Declarer;
import org.streampipes.container.declarer.SemanticEventConsumerDeclarer;
import org.streampipes.container.declarer.SemanticEventProcessingAgentDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;

public class DeclarersSingleton {
    private static DeclarersSingleton instance;

    private List<SemanticEventProcessingAgentDeclarer> epaDeclarers;
    private List<SemanticEventProducerDeclarer> producerDeclarers;
    private List<SemanticEventConsumerDeclarer> consumerDeclarers;
    private int port;
    private String route;
    private String hostName;


    private DeclarersSingleton() {
        this.epaDeclarers = new ArrayList<>();
        this.producerDeclarers = new ArrayList<>();
        this.consumerDeclarers = new ArrayList<>();
        this.route = "/";
    }

    public static DeclarersSingleton getInstance() {
        if (DeclarersSingleton.instance == null) {
            DeclarersSingleton.instance = new DeclarersSingleton();
        }
        return DeclarersSingleton.instance;
    }

    public void addDeclarers(List<Declarer> allDeclarers) {
        for (Declarer d : allDeclarers) {
            add(d);
        }
    }

    public DeclarersSingleton add(Declarer d) {
        if (d instanceof SemanticEventProcessingAgentDeclarer) {
            addEpaDeclarer((SemanticEventProcessingAgentDeclarer) d);
        } else if (d instanceof SemanticEventProducerDeclarer) {
            addProducerDeclarer((SemanticEventProducerDeclarer) d);
        } else if (d instanceof SemanticEventConsumerDeclarer) {
            addConsumerDeclarer((SemanticEventConsumerDeclarer) d);
        }

        return getInstance();
    }

    public List<Declarer> getDeclarers() {
        List<Declarer> result = new ArrayList<>();
        result.addAll(epaDeclarers);
        result.addAll(producerDeclarers);
        result.addAll(consumerDeclarers);
        return result;
    }

    public void addEpaDeclarer(SemanticEventProcessingAgentDeclarer epaDeclarer) {
        epaDeclarers.add(epaDeclarer);
    }

    public void addProducerDeclarer(SemanticEventProducerDeclarer sourceDeclarer) {
    	checkAndStartExecutableStreams(sourceDeclarer);
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

    public void setPort(int port) {
        this.port = port;
    }

    public void setHostName(String host) {
        this.hostName = host;
    }

    public void setRoute(String route) {
        this.route = "/" + route + "/";
    }

    public String getBaseUri() {
        return "http://" + hostName + ":" + port + route;
    }
    
    private void checkAndStartExecutableStreams(SemanticEventProducerDeclarer sourceDeclarer) {
		sourceDeclarer.getEventStreams()
			.stream()
			.filter(s -> s.isExecutable())
			.forEach(es -> { es.declareModel(sourceDeclarer.declareModel()); es.executeStream(); });
	}
}
