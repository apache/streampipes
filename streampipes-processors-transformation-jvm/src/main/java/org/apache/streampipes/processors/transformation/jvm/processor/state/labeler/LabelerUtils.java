package org.apache.streampipes.processors.transformation.jvm.processor.state.labeler;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.vocabulary.SPSensor;

import java.util.List;

public class LabelerUtils {

    public static EventSchema resolveOutputStrategy(DataProcessorInvocation processingElement, String labelName, List<String> labelStrings) throws SpRuntimeException {

        List<EventProperty> properties = processingElement
                .getInputStreams()
                .get(0)
                .getEventSchema()
                .getEventProperties();

        properties.add(PrimitivePropertyBuilder
                .create(Datatypes.String, labelName)
                .valueSpecification(labelName, "possible label values", labelStrings)
                .domainProperty(SPSensor.STATE)
                .scope(PropertyScope.DIMENSION_PROPERTY)
                .build());

        return new EventSchema(properties);
    }
}
