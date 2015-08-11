package de.fzi.cep.sepa.manager.monitoring;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import de.fzi.cep.sepa.manager.monitoring.job.JobManager;
import de.fzi.cep.sepa.manager.monitoring.job.SepaMonitoringJob;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.storage.controller.StorageManager;

public class MonitoringJobTest {

	private SepaDescription sepaDescription;
	

	@Before
	public void setUp() throws URISyntaxException {
//		this.sepaDescription = StorageManager.INSTANCE.getStorageAPI().getSEPAById("http://localhost:8090/sepa/movement");
	}
	
	@Test
	public void createMonitoringJob()
	{
//		SepaMonitoringJob monitoringJob = new SepaMonitoringJob(sepaDescription);
//		JobManager.INSTANCE.addJob(monitoringJob);
	}
}
