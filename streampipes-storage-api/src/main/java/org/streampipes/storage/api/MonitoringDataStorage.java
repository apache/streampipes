package org.streampipes.storage.api;

import java.util.List;

import org.streampipes.model.client.monitoring.JobReport;

public interface MonitoringDataStorage {

	List<JobReport> getAllMonitoringJobReports();
	
	List<JobReport> getAllMonitoringJobReportsByElement(String elementUri);
	
	JobReport getLatestJobReport(String elementUri);
	
	boolean storeJobReport(JobReport jobReport);
	
	
}
