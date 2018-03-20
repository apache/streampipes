/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.container.html.page;

import java.net.URI;
import java.util.List;

import org.streampipes.container.declarer.SemanticEventConsumerDeclarer;
import org.streampipes.container.html.model.AgentDescription;
import org.streampipes.container.html.model.Description;

@Deprecated
public class EventConsumerWelcomePage extends WelcomePageGenerator<SemanticEventConsumerDeclarer>{

	
	public EventConsumerWelcomePage(String baseUri, List<SemanticEventConsumerDeclarer> declarers)
	{
		super(baseUri, declarers);
	}
	
	@Override
	public List<Description> buildUris() {
		for(SemanticEventConsumerDeclarer declarer : declarers)
		{
			Description producer = new AgentDescription();
			producer.setName(declarer.declareModel().getName());
			producer.setDescription(declarer.declareModel().getDescription());
			producer.setUri(URI.create(baseUri +declarer.declareModel().getUri().replaceFirst("[a-zA-Z]{4}://[a-zA-Z\\.]+:\\d+/", "")));
			descriptions.add(producer);
		}
		return descriptions;	
	}
}
