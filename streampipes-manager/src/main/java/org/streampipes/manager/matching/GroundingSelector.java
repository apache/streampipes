package org.streampipes.manager.matching;

import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.base.NamedStreamPipesEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by riemer on 23.09.2016.
 */
public abstract class GroundingSelector {

    protected NamedStreamPipesEntity source;
    protected Set<InvocableStreamPipesEntity> targets;

    public GroundingSelector(NamedStreamPipesEntity source, Set<InvocableStreamPipesEntity> targets) {
        this.source = source;
        this.targets = targets;
    }

    protected List<InvocableStreamPipesEntity> buildInvocables() {
        List<InvocableStreamPipesEntity> elements = new ArrayList<>();
        elements.add((InvocableStreamPipesEntity) source);
        elements.addAll(targets);

        return elements;
    }
}
