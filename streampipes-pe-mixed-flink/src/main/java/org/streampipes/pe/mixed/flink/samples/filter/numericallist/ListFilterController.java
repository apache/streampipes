package org.streampipes.pe.mixed.flink.samples.filter.numericallist;

import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.pe.mixed.flink.samples.FlinkConfig;
import org.streampipes.sdk.StaticProperties;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.Options;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.sdk.utils.Datatypes;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

import java.util.List;

public class ListFilterController extends FlinkDataProcessorDeclarer<ListFilterParameters> {

  private static final String ALL_VALUES = "All values satisfy filter condition";
  private static final String ANY_VALUE = "At least one value satisfies filter condition";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("filter-list-number", "List Filter (Numeric)", "")
            .category(DataProcessorType.FILTER)
            .iconUrl(FlinkConfig.getIconUrl("Numerical_Filter_Icon_HQ"))
            .requiredPropertyStream1WithUnaryMapping(EpRequirements.listRequirement(Datatypes.Number), "number",
                    "Specifies the field name where" +
                    " the filter operation should be applied on.", "")
            .outputStrategy(OutputStrategies.keep())
            .requiredSingleValueSelection("operation", "Filter Operation", "Specifies the filter " +
                    "operation that should be applied on the field", Options.from("<", "<=", ">", ">=", "=="))
            .requiredSingleValueSelection("filter-strategy", "Filter Settings", "", Options.from(ALL_VALUES,
                    ANY_VALUE))
            .requiredParameterAsCollection(Labels.from("value",
                    "Threshold value", "Specifies a" +
                            " threshold value. Multiple thresholds can be used if you want to find elements in the " +
                            "form VALUE > (THRESHOLD-A OR THRESHOLD-B)"), StaticProperties
                    .doubleFreeTextProperty
                    ("",
                    "", ""))
            .supportedProtocols(SupportedProtocols.kafka())
            .supportedFormats(SupportedFormats.jsonFormat())
            .build();
  }

  @Override
  protected FlinkDataProcessorRuntime<ListFilterParameters> getRuntime(DataProcessorInvocation graph) {
    ProcessingElementParameterExtractor extractor = ProcessingElementParameterExtractor.from(graph);

    List<Double> filterKeywords = extractor.singleValueParameterFromCollection("value", Double.class);
    String stringOperation = extractor.selectedSingleValue("operation", String.class);
    String stringFilterSettings = extractor.selectedSingleValue("filter-strategy", String.class);

    FilterOperation filterOperation = FilterOperation.GT;
    FilterSettings filterSettings = FilterSettings.ALL_VALUES;

    if (stringOperation.equals("<=")) {
      filterOperation = FilterOperation.LE;
    } else if (stringOperation.equals("<")) {
      filterOperation = FilterOperation.LT;
    } else if (stringOperation.equals(">=")) {
      filterOperation = FilterOperation.GE;
    } else if (stringOperation.equals("==")) {
      filterOperation = FilterOperation.EQ;
    }

    if (stringFilterSettings.equals(ANY_VALUE)) {
      filterSettings = FilterSettings.ANY_VALUE;
    }

    String filterProperty = extractor.mappingPropertyValue("number");

    ListFilterParameters staticParam = new ListFilterParameters(graph, filterProperty, filterKeywords,
            filterOperation, filterSettings);

    return new ListFilterProgram(staticParam, new FlinkDeploymentConfig(FlinkConfig.JAR_FILE,
            FlinkConfig.INSTANCE.getFlinkHost(), FlinkConfig.INSTANCE.getFlinkPort()));
  }
}
