package org.apache.streampipes.processors.transformation.jvm.processor.state;

import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.OneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;

import java.util.List;
import java.util.stream.Collectors;

public class StateUtils {

    public static final String LABEL_NAME = "labelName";
    public static final String LABEL_COLLECTION_ID = "labelCollectionId";
    public static final String NUMBER_VALUE_ID = "numberValueId";
    public static final String COMPARATOR_ID = "comparatorId";
    public static final String LABEL_STRING_ID = "labelStringId";


    public static String getLabelName(ProcessingElementParameterExtractor extractor) {
        return extractor.textParameter(LABEL_NAME);
    }

    public static List<StaticPropertyGroup> getGroupItems(ProcessingElementParameterExtractor extractor) {
        return extractor.collectionMembersAsGroup(LABEL_COLLECTION_ID);
    }

    public static List<Double> getNumberValues(ProcessingElementParameterExtractor extractor) {
        return getGroupItems(extractor)
                .stream()
                .map(group -> (
                        extractor
                                .extractGroupMember(NUMBER_VALUE_ID, group)
                                .as(FreeTextStaticProperty.class))
                        .getValue())
                .map(Double::parseDouble)
                .collect(Collectors.toList());
    }

    public static List<String> getLabelStrings(ProcessingElementParameterExtractor extractor) {
        return getGroupItems(extractor)
                .stream()
                .map(group -> (extractor
                        .extractGroupMember(LABEL_STRING_ID, group)
                        .as(FreeTextStaticProperty.class))
                        .getValue())
                .collect(Collectors.toList());
    }

    public static List<String> getComparators(ProcessingElementParameterExtractor extractor) {
        return getGroupItems(extractor)
                .stream()
                .map(group -> (extractor
                        .extractGroupMember(COMPARATOR_ID, group)
                        .as(OneOfStaticProperty.class))
                        .getOptions()
                        .stream()
                        .filter(Option::isSelected).findFirst().get().getName())
                .collect(Collectors.toList());
    }


}
