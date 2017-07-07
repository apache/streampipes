package de.fzi.cep.sepa.sdk.builder;

import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.sdk.helpers.Label;

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

    protected <SP extends StaticProperty> SP prepareStaticProperty(Label label, SP element) {
        element.setInternalName(label.getInternalId());
        element.setDescription(label.getDescription());
        element.setLabel(label.getLabel());

        return element;
    }

    protected abstract BU me();

    protected abstract void prepareBuild();

    public T build() {
        prepareBuild();
        return elementDescription;
    }
}
