package org.streampipes.manager.monitoring.job;

import com.google.gson.Gson;
import org.streampipes.model.client.monitoring.JobReport;
import org.streampipes.model.client.monitoring.TaskReport;
import org.streampipes.storage.management.StorageDispatcher;

import java.util.Date;
import java.util.List;

public class MonitoringJobExecutor implements Runnable {

	MonitoringJob<?> job;
	
	public MonitoringJobExecutor(MonitoringJob<?> job) {
		this.job = job;
	}
	
	@Override
	public void run() {
		List<TaskReport> reports = job.performJobExecution();
		reports.forEach(r -> System.out.println(r.toString()));
		JobReport jobReport = new JobReport(job.getElementId(), new Date(), reports);
		StorageDispatcher.INSTANCE.getNoSqlStore().getMonitoringDataStorageApi().storeJobReport(jobReport);
		System.out.println(new Gson().toJson(jobReport));
	}
	
}
