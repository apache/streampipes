package org.streampipes.sdk.builder;

import org.streampipes.model.impl.graph.SecDescription;

/**
 * Created by riemer on 04.12.2016.
 */
public class DataSinkBuilder extends AbstractProcessingElementBuilder<DataSinkBuilder, SecDescription> {

    protected DataSinkBuilder(String id, String label, String description) {
        super(id, label, description, new SecDescription());
    }

    public static DataSinkBuilder create(String id, String label, String description)
    {
        return new DataSinkBuilder(id, label, description);
    }

    @Override
    protected DataSinkBuilder me() {
        return this;
    }
}
