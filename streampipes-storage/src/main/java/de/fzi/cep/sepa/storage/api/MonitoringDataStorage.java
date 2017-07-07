package de.fzi.cep.sepa.storage.api;

import java.util.List;

import de.fzi.cep.sepa.model.client.monitoring.JobReport;

public interface MonitoringDataStorage {

	List<JobReport> getAllMonitoringJobReports();
	
	List<JobReport> getAllMonitoringJobReportsByElement(String elementUri);
	
	JobReport getLatestJobReport(String elementUri);
	
	boolean storeJobReport(JobReport jobReport);
	
	
}
