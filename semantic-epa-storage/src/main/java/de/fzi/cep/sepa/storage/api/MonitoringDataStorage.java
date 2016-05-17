package de.fzi.cep.sepa.storage.api;

import java.util.List;

import de.fzi.cep.sepa.model.client.monitoring.JobReport;

public interface MonitoringDataStorage {

	public List<JobReport> getAllMonitoringJobReports();
	
	public List<JobReport> getAllMonitoringJobReportsByElement(String elementUri);
	
	public JobReport getLatestJobReport(String elementUri);
	
	public boolean storeJobReport(JobReport jobReport);
	
	
}
