package de.fzi.cep.sepa.manager.monitoring.job;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.fzi.cep.sepa.storage.controller.StorageManager;

public enum JobManager {
	
	INSTANCE;

	private List<MonitoringJob<?>> currentJobs;
	
	private final ScheduledExecutorService scheduler;
	
	JobManager()
	{
		this.currentJobs = new ArrayList<>();
		this.scheduler = Executors.newScheduledThreadPool(2);
	}
	
	public void addJob(MonitoringJob<?> job)
	{
		currentJobs.add(job);
		scheduler.scheduleAtFixedRate(new MonitoringJobExecutor(job), 10, 10, TimeUnit.MINUTES);
	}
	
	public void removeJob(MonitoringJob<?> job)
	{
		currentJobs.remove(job);
	}
	
	public List<MonitoringJob<?>> getCurrentJobs()
	{
		return currentJobs;
	}
	
	public void prepareMonitoring() {
		StorageManager.INSTANCE.getStorageAPI().getAllSEPAs().forEach(s -> addJob(new SepaMonitoringJob(s)));
		StorageManager.INSTANCE.getStorageAPI().getAllSECs().forEach(s -> addJob(new SecMonitoringJob(s)));
		//TODO: add seps StorageManager.INSTANCE.getStorageAPI().getAllSECs().forEach(s -> currentJobs.add(new SecMonitoringJob(s)));
	}
}
