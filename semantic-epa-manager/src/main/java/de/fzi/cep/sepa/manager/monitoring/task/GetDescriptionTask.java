package de.fzi.cep.sepa.manager.monitoring.task;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import de.fzi.cep.sepa.manager.monitoring.job.MonitoringUtils;
import de.fzi.cep.sepa.model.ConsumableSEPAElement;
import de.fzi.cep.sepa.model.client.monitoring.TaskReport;

public class GetDescriptionTask extends TaskDefinition {
	
	private ConsumableSEPAElement element;
	
	private static final String TASK_NAME = "HTTP Get Availability Test";
	
	public GetDescriptionTask(ConsumableSEPAElement element)
	{
		super();
		this.element = element;
	}
	

	@Override
	public void executeBefore() {
		// TODO Auto-generated method stub
	}

	@Override
	public void executeAfter() {
		// TODO Auto-generated method stub
	}

	@Override
	public TaskReport defineTaskExecution() {
		try {
			int statusCode = MonitoringUtils.getHttpResponse(element.getUri()).getStatusLine().getStatusCode();
			if (statusCode == 200) {
				return successMsg(TASK_NAME);
			}
			else
			{
				return errorMsg(TASK_NAME, "Wrong status code");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return errorMsg(TASK_NAME, e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			return errorMsg(TASK_NAME, e.getMessage());
		}
	}
}
