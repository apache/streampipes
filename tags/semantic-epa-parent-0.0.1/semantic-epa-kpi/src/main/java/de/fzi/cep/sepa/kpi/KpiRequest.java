package de.fzi.cep.sepa.kpi;

public class KpiRequest {

	private String kpiId;
	private String kpiName;
	private String kpiDescription;
	
	private KpiOperationType kpiOperation;
	private Operation operation;
	
	
	
	public String getKpiId() {
		return kpiId;
	}
	public void setKpiId(String kpiId) {
		this.kpiId = kpiId;
	}
	public String getKpiName() {
		return kpiName;
	}
	public void setKpiName(String kpiName) {
		this.kpiName = kpiName;
	}
	public String getKpiDescription() {
		return kpiDescription;
	}
	public void setKpiDescription(String kpiDescription) {
		this.kpiDescription = kpiDescription;
	}
	public KpiOperationType getKpiOperation() {
		return kpiOperation;
	}
	public void setKpiOperation(KpiOperationType kpiOperation) {
		this.kpiOperation = kpiOperation;
	}
	public Operation getOperation() {
		return operation;
	}
	public void setOperation(Operation operation) {
		this.operation = operation;
	}
	
	
}
