package de.fzi.cep.sepa.esper.enrich.math;

import java.util.List;

import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class MathParameter extends BindingParameters {

	private List<String> selectProperties;
	private Operation operation;
	private String leftOperand;
	private String rightOperand; 
	private String appendPropertyName;
	
	public MathParameter(String inName, String outName,
			List<String> allProperties, List<String> partitionProperties, List<String> selectProperties, Operation operation, String leftOperand, String rightOperand, String appendPropertyName) {
		super(inName, outName, allProperties, partitionProperties);
		this.selectProperties = selectProperties;
		this.operation = operation;
		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;
		this.appendPropertyName = appendPropertyName;
	}

	public List<String> getSelectProperties() {
		return selectProperties;
	}

	public void setSelectProperties(List<String> selectProperties) {
		this.selectProperties = selectProperties;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public String getLeftOperand() {
		return leftOperand;
	}

	public void setLeftOperand(String leftOperand) {
		this.leftOperand = leftOperand;
	}

	public String getRightOperand() {
		return rightOperand;
	}

	public void setRightOperand(String rightOperand) {
		this.rightOperand = rightOperand;
	}

	public String getAppendPropertyName() {
		return appendPropertyName;
	}

	public void setAppendPropertyName(String appendPropertyName) {
		this.appendPropertyName = appendPropertyName;
	}

}
