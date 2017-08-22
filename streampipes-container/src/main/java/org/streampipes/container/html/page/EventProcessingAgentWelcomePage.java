package org.streampipes.container.html.page;

import java.net.URI;
import java.util.List;

import org.streampipes.container.declarer.SemanticEventProcessingAgentDeclarer;
import org.streampipes.container.html.model.AgentDescription;
import org.streampipes.container.html.model.Description;

@Deprecated
public class EventProcessingAgentWelcomePage extends WelcomePageGenerator<SemanticEventProcessingAgentDeclarer> {

	public EventProcessingAgentWelcomePage(String baseUri, List<SemanticEventProcessingAgentDeclarer> declarers)
	{
		super(baseUri, declarers);
	}
	
	@Override
	public List<Description> buildUris() {
		for(SemanticEventProcessingAgentDeclarer declarer : declarers)
		{
			AgentDescription producer = new AgentDescription();
			producer.setName(declarer.declareModel().getName());
			producer.setDescription(declarer.declareModel().getDescription());
			producer.setUri(URI.create(baseUri +declarer.declareModel().getUri().replaceFirst("[a-zA-Z]{4}://[a-zA-Z\\.]+:\\d+/", "")));
			descriptions.add(producer);
		}
		return descriptions;
	}

}
