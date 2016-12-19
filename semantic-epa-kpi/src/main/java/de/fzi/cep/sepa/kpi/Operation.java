package de.fzi.cep.sepa.kpi;

public abstract class Operation {

	public Operation() {

	}

	protected OperationType operationType;

	public OperationType getOperationType() {
		return operationType;
	}

	public void setOperationType(OperationType operationType) {
		this.operationType = operationType;
	}
	
	
}
