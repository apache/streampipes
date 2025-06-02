package org.apache.streampipes.processors.transformation.jvm.processor.switchoperator;


// Inner class to store switch case entries
public class SwitchCaseEntry {
  private final String caseValue;
  private String operator = "equals";
  private final Object outputValue;

  public SwitchCaseEntry(String caseValue, Object outputValue) {
    this.caseValue = caseValue;
    this.outputValue = outputValue;
  }

  public SwitchCaseEntry(String caseValue, Object outputValue, String operator) {
    this.caseValue = caseValue;
    this.outputValue = outputValue;
    this.operator = operator;
  }

  public String getCaseValue() {
    return caseValue;
  }

  public Object getOutputValue() {
    return outputValue;
  }

  public String getOperator() {
    return operator;
  }
}
