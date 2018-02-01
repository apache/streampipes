package org.streampipes.storage.api;

import org.streampipes.model.client.monitoring.JobReport;

import java.util.List;

public interface IPipelineMonitoringDataStorage {

	List<JobReport> getAllMonitoringJobReports();
	
	List<JobReport> getAllMonitoringJobReportsByElement(String elementUri);
	
	JobReport getLatestJobReport(String elementUri);
	
	boolean storeJobReport(JobReport jobReport);
	
	
}
