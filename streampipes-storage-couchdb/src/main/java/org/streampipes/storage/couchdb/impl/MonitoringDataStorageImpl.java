package org.streampipes.storage.couchdb.impl;

import org.streampipes.model.client.monitoring.JobReport;
import org.streampipes.storage.api.IPipelineMonitoringDataStorage;
import org.streampipes.storage.couchdb.dao.AbstractDao;
import org.streampipes.storage.couchdb.utils.Utils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MonitoringDataStorageImpl extends AbstractDao<JobReport> implements IPipelineMonitoringDataStorage {

	public MonitoringDataStorageImpl() {
		super(Utils::getCouchDbMonitoringClient, JobReport.class);
	}

	@Override
	public List<JobReport> getAllMonitoringJobReports() {
		return findAll();
	}

	@Override
	public List<JobReport> getAllMonitoringJobReportsByElement(String elementUri) {
		List<JobReport> allReports = findAll();
		return getJobReportStream(elementUri, allReports)
						.collect(Collectors.toList());
	}

	private Stream<JobReport> getJobReportStream(String elementUri, List<JobReport> allReports) {
		return allReports
						.stream()
						.filter(r -> r.getElementId().equals(elementUri));
	}

	@Override
	public JobReport getLatestJobReport(String elementUri) {
		List<JobReport> allReports = findAll();
		return getJobReportStream(elementUri, allReports)
						.sorted(Comparator.comparing(JobReport::getGenerationDate))
						.findFirst()
						.get();
	}

	@Override
	public boolean storeJobReport(JobReport jobReport) {
		return persist(jobReport);
	}

}
