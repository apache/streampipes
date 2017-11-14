package org.streampipes.sdk.builder;

import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.sdk.helpers.Label;

/**
 * Created by riemer on 04.12.2016.
 */
public abstract class AbstractPipelineElementBuilder<BU extends AbstractPipelineElementBuilder<BU, T>, T extends NamedStreamPipesEntity> {

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
