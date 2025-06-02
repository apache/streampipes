package org.apache.streampipes.processors.transformation.jvm.processor.switchoperator;

import org.apache.streampipes.extensions.api.pe.IStreamPipesDataProcessor;

public interface IStreamPipesSwitchProcessor extends IStreamPipesDataProcessor {

  String SWITCH_FILTER_OUTPUT_KEY = "switch-filter-result";
  String SWITCH_FILTER_KEY = "switch-filter-key";
  String SWITCH_CASE_VALUE_OPERATOR = "switch-case-value-operator";
  String SWITCH_CASE_VALUE = "switch-case-value";
  String SWITCH_CASE_VALUE_OUTPUT = "switch-case-value-output";
  String SWITCH_CASE_GROUP = "switch-case-group";
  String OUTPUT_TYPE_KEY = "output-type";
  String SWITCH_CASE_VALUE_DEFAULT_OUTPUT = "switch-case-value-default-output";

}
