package de.fzi.cep.sepa.client.init;

public abstract class ModelSubmitter {
    //TODO make dynamic and load the value from config
    public static String getBaseUri() {
        return "http://localhost:8081/stream-story/api/v1.1.1/";
    }

    /**
     * This Method needs to be implemented to instantiate an client container
     * Use the DeclarersSingleton to register the declarers
     */
    public abstract void init();
}
