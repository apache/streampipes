package de.fzi.cep.sepa.sdk.epa;

import de.fzi.cep.sepa.model.impl.graph.SepaDescription;

/**
 * Created by riemer on 20.11.2016.
 */
public class ProcessingElementBuilder {

    private SepaDescription sepaDescription;

    private ProcessingElementBuilder(String id, String name, String description) {
        this.sepaDescription = new SepaDescription();
    }

    public static ProcessingElementBuilder create(String id, String label, String description)
    {
        return new ProcessingElementBuilder(id, label, description);
    }

    public ProcessingElementBuilder iconUrl(String iconUrl) {
        sepaDescription.setIconUrl(iconUrl);
        return this;
    }

    public SepaDescription build() {
        return sepaDescription;
    }
}
