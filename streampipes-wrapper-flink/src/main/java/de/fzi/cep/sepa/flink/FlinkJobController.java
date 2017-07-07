package de.fzi.cep.sepa.flink;

import akka.actor.ActorSystem;
import org.apache.flink.api.common.JobID;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.akka.AkkaUtils;
import org.apache.flink.runtime.client.JobStatusMessage;
import org.apache.flink.runtime.instance.ActorGateway;
import org.apache.flink.runtime.jobgraph.JobStatus;
import org.apache.flink.runtime.leaderretrieval.LeaderRetrievalService;
import org.apache.flink.runtime.messages.JobManagerMessages;
import org.apache.flink.runtime.messages.JobManagerMessages.CancelJob;
import org.apache.flink.runtime.messages.JobManagerMessages.RunningJobsStatus;
import org.apache.flink.runtime.util.LeaderRetrievalUtils;
import scala.Some;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FlinkJobController {

	private final static FiniteDuration askTimeout = new FiniteDuration(120000, TimeUnit.MILLISECONDS);
	private final static FiniteDuration lookupTimeout = new FiniteDuration(120000, TimeUnit.MILLISECONDS);
	
	private final Configuration config;
	
	public FlinkJobController(String hostname, int port) {
		this.config = getConfig(hostname, port);
	}
	
	public ActorGateway getJobManagerGateway() throws Exception {
		
		ActorSystem actorSystem;
		scala.Tuple2<String, Object> systemEndpoint = new scala.Tuple2<String, Object>("", 0);
		
		try {
			actorSystem = AkkaUtils.createActorSystem(
					config,
					new Some<scala.Tuple2<String, Object>>(systemEndpoint));
		}
		catch (Exception e) {
			throw new IOException("Could not start actor system to communicate with JobManager", e);
		}

		LeaderRetrievalService lrs = LeaderRetrievalUtils.createLeaderRetrievalService(config);

		return LeaderRetrievalUtils.retrieveLeaderGateway(lrs, actorSystem, lookupTimeout);
	}
	
	public JobID findJobId(ActorGateway jobManagerGateway, String jobName) throws Exception {
		
		Future<Object> response = jobManagerGateway.ask(
				JobManagerMessages.getRequestRunningJobsStatus(),
				askTimeout);
	
		Object result;
		try {
			result = Await.result(response, askTimeout);
		}
		catch (Exception e) {
			throw new Exception("Could not retrieve running jobs from the JobManager.", e);
		}
	
		if (result instanceof RunningJobsStatus) {
		
			List<JobStatusMessage> jobs = ((RunningJobsStatus) result).getStatusMessages();
		
			for (JobStatusMessage rj : jobs) {
				if (rj.getJobState().equals(JobStatus.RUNNING)
						|| rj.getJobState().equals(JobStatus.RESTARTING)) {
					if (rj.getJobName().equals(jobName)) return rj.getJobId();
				}
			}
		}	
		
		throw new Exception("Could not find job");
	}	
	
	public boolean deleteJob(JobID jobId) {
		
		try {
			ActorGateway jobManager = getJobManagerGateway();
			Future<Object> response = jobManager.ask(new CancelJob(jobId), askTimeout);
			Await.result(response, askTimeout);
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	private final Configuration getConfig(String hostname, int port) {
		
		Configuration config = new Configuration();
		config.setString(ConfigConstants.JOB_MANAGER_IPC_ADDRESS_KEY, hostname);
		config.setInteger(ConfigConstants.JOB_MANAGER_IPC_PORT_KEY, port);
		
		return config;
		
	}
}
