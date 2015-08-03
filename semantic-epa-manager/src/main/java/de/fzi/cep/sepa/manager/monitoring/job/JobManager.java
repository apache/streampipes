package de.fzi.cep.sepa.manager.monitoring.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import de.fzi.cep.sepa.storage.controller.StorageManager;

public enum JobManager {
	
	INSTANCE;

	private List<MonitoringJob<?>> currentJobs;
	
	JobManager()
	{
		this.currentJobs = new ArrayList<>();
	}
	
	public void addJob(MonitoringJob<?> job)
	{
		currentJobs.add(job);
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new MonitoringJobExecutor(job), new Date().getTime(), job.repeatAfter);
	}
	
	public void removeJob(MonitoringJob<?> job)
	{
		currentJobs.remove(job);
	}
	
	public void prepareMonitoring() {
		StorageManager.INSTANCE.getStorageAPI().getAllSEPAs().forEach(s -> currentJobs.add(new SepaMonitoringJob(s)));
		StorageManager.INSTANCE.getStorageAPI().getAllSECs().forEach(s -> currentJobs.add(new SecMonitoringJob(s)));
		//TODO: add seps StorageManager.INSTANCE.getStorageAPI().getAllSECs().forEach(s -> currentJobs.add(new SecMonitoringJob(s)));
	}
}
