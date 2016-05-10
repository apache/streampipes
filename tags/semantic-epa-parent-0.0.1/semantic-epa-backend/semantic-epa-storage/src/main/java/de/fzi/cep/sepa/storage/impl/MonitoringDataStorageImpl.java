package de.fzi.cep.sepa.storage.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.lightcouch.CouchDbClient;
import org.lightcouch.Response;

import de.fzi.cep.sepa.model.client.monitoring.JobReport;
import de.fzi.cep.sepa.storage.api.MonitoringDataStorage;
import de.fzi.cep.sepa.storage.util.Utils;

public class MonitoringDataStorageImpl implements MonitoringDataStorage {

	@Override
	public List<JobReport> getAllMonitoringJobReports() {
		CouchDbClient dbClient = Utils.getCouchDbMonitoringClient();
		List<JobReport> jobReports = dbClient.view("_all_docs").includeDocs(true)
				.query(JobReport.class);

		return jobReports;
	}

	@Override
	public List<JobReport> getAllMonitoringJobReportsByElement(String elementUri) {
		List<JobReport> allReports = getAllMonitoringJobReports();
		return allReports.stream().filter(r -> r.getElementId().equals(elementUri)).collect(Collectors.toList());
	}

	@Override
	public JobReport getLatestJobReport(String elementUri) {
		List<JobReport> allReports = getAllMonitoringJobReports();
		return allReports.stream().filter(r -> r.getElementId().equals(elementUri)).sorted((r1, r2) -> r1.getGenerationDate().compareTo(r2.getGenerationDate())).findFirst().get();
	}

	@Override
	public boolean storeJobReport(JobReport jobReport) {
		CouchDbClient dbClient = Utils.getCouchDbMonitoringClient();
		Response response = dbClient.save(jobReport);
		dbClient.shutdown();
		if (response.getError() != null)
			return false;
		return true;
	}

}
