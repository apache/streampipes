package org.apache.streampipes.processors.transformation.jvm.processor.switchoperator;

public class NumericalSwitchCaseEntry extends SwitchCaseEntry {
  private final String operator;

  public NumericalSwitchCaseEntry(String caseValue, Object outputValue, String operator) {
    super(caseValue, outputValue); // Call base constructor for common fields
    this.operator = operator;
  }

  public String getOperator() {
    return operator;
  }
}