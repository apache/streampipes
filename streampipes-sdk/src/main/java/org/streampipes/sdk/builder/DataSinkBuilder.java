package org.streampipes.sdk.builder;

import org.streampipes.model.DataSinkType;
import org.streampipes.model.graph.DataSinkDescription;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by riemer on 04.12.2016.
 */
public class DataSinkBuilder extends AbstractProcessingElementBuilder<DataSinkBuilder, DataSinkDescription> {

    protected DataSinkBuilder(String id, String label, String description) {
        super(id, label, description, new DataSinkDescription());
    }

    public static DataSinkBuilder create(String id, String label, String description)
    {
        return new DataSinkBuilder(id, label, description);
    }

    public DataSinkBuilder category(DataSinkType... categories) {
        this.elementDescription
                .setCategory(Arrays
                        .stream(categories)
                        .map(Enum::name)
                        .collect(Collectors.toList()));
        return me();
    }

    @Override
    protected DataSinkBuilder me() {
        return this;
    }
}
