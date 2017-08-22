package org.streampipes.storage.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.streampipes.model.client.monitoring.JobReport;
import org.streampipes.storage.api.MonitoringDataStorage;
import org.streampipes.storage.util.Utils;
import org.lightcouch.CouchDbClient;

public class MonitoringDataStorageImpl extends Storage<JobReport> implements MonitoringDataStorage {

	public MonitoringDataStorageImpl() {
		super(JobReport.class);
	}

	@Override
	public List<JobReport> getAllMonitoringJobReports() {
		return getAll();
	}

	@Override
	public List<JobReport> getAllMonitoringJobReportsByElement(String elementUri) {
		List<JobReport> allReports = getAll();
		return allReports.stream().filter(r -> r.getElementId().equals(elementUri)).collect(Collectors.toList());
	}

	@Override
	public JobReport getLatestJobReport(String elementUri) {
		List<JobReport> allReports = getAll();
		return allReports.stream().filter(r -> r.getElementId().equals(elementUri)).sorted((r1, r2) -> r1.getGenerationDate().compareTo(r2.getGenerationDate())).findFirst().get();
	}

	@Override
	public boolean storeJobReport(JobReport jobReport) {
		return add(jobReport);
	}

	@Override
	protected CouchDbClient getCouchDbClient() {
		return Utils.getCouchDbMonitoringClient();
	}
}
