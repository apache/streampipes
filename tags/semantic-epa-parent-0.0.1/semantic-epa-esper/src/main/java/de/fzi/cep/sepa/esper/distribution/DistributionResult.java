package de.fzi.cep.sepa.esper.distribution;

import java.util.List;

public class DistributionResult {

	List<DistributionRow> rows;

	public DistributionResult(List<DistributionRow> rows) {
		super();
		this.rows = rows;
	}

	public List<DistributionRow> getRows() {
		return rows;
	}

	public void setRows(List<DistributionRow> rows) {
		this.rows = rows;
	}
	
	
}
