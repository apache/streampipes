package de.fzi.cep.sepa.manager.execution.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.fzi.cep.sepa.model.client.pipeline.PipelineStatusMessage;

public class PipelineStatusManager {

	private static Map<String, List<PipelineStatusMessage>> pipelineStatusMessages = new HashMap<>();
	
	public static void addPipelineStatus(String pipelineId, PipelineStatusMessage message) {
		if (isInitialized(pipelineId))
			pipelineStatusMessages.get(pipelineId).add(message);
		else {
			List<PipelineStatusMessage> statusMessageList = new ArrayList<>();
			statusMessageList.add(message);
			pipelineStatusMessages.put(pipelineId, statusMessageList);
		}
		
	}
	
	private static boolean isInitialized(String pipelineId) {
		return pipelineStatusMessages.containsKey(pipelineId);
	}

	public static List<PipelineStatusMessage> getPipelineStatus(String pipelineId) {
		if (!isInitialized(pipelineId)) return new ArrayList<>();
		else return pipelineStatusMessages.get(pipelineId);
	}
	
	public static List<PipelineStatusMessage> getPipelineStatus(String pipelineId, int numberOfLatestEntries) {
		List<PipelineStatusMessage> messages = getPipelineStatus(pipelineId);
		int statusMessageCount = messages.size();
		if (statusMessageCount <= numberOfLatestEntries) return messages;
		else return messages.subList(messages.size()-numberOfLatestEntries, messages.size());
	}
}
