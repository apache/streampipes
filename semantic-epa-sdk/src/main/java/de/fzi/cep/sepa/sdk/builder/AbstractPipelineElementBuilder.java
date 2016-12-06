package de.fzi.cep.sepa.sdk.builder;

import de.fzi.cep.sepa.model.NamedSEPAElement;

/**
 * Created by riemer on 04.12.2016.
 */
public abstract class AbstractPipelineElementBuilder<BU extends AbstractPipelineElementBuilder<BU, T>, T extends NamedSEPAElement> {

    protected T elementDescription;

    protected AbstractPipelineElementBuilder(String id, String label, String description, T element) {
        this.elementDescription = element;
        this.elementDescription.setElementId(id);
        this.elementDescription.setName(label);
        this.elementDescription.setDescription(description);
    }

    public BU iconUrl(String iconUrl) {
        elementDescription.setIconUrl(iconUrl);
        return me();
    }

    protected abstract BU me();

    protected abstract void prepareBuild();

    public T build() {
        prepareBuild();
        return elementDescription;
    }
}
