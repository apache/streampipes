package de.fzi.cep.sepa.manager.monitoring.job;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import de.fzi.cep.sepa.model.client.monitoring.JobReport;
import de.fzi.cep.sepa.model.client.monitoring.TaskReport;
import de.fzi.cep.sepa.storage.controller.StorageManager;

public class MonitoringJobExecutor extends TimerTask {

	MonitoringJob<?> job;
	
	public MonitoringJobExecutor(MonitoringJob<?> job) {
		this.job = job;
	}
	
	@Override
	public void run() {
		List<TaskReport> reports = job.performJobExecution();
		reports.forEach(r -> System.out.println(r.toString()));
		JobReport jobReport = new JobReport(job.getElementId(), new Date(), reports);
		StorageManager.INSTANCE.getMonitoringDataStorageApi().storeJobReport(jobReport);
	}
	
}
