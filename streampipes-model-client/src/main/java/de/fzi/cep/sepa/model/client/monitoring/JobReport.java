package de.fzi.cep.sepa.model.client.monitoring;

import java.util.Date;
import java.util.List;

public class JobReport {

	// contains report of a single job
	// TODO needed?
	
	//TODO store in database and create queries
	
	private String elementId;
	private Date generationDate;
	private List<TaskReport> taskReports;
	
	public JobReport(String elementId, Date generationDate, List<TaskReport> taskResults)
	{
		this.generationDate = generationDate;
		this.taskReports = taskResults;
		this.elementId = elementId;
	}

	public Date getGenerationDate() {
		return generationDate;
	}

	public void setGenerationDate(Date generationDate) {
		this.generationDate = generationDate;
	}

	public List<TaskReport> getTaskReports() {
		return taskReports;
	}

	public void setTaskReports(List<TaskReport> taskReports) {
		this.taskReports = taskReports;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}	
}
