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

package org.streampipes.sdk.stream;

import org.streampipes.commons.Utils;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.schema.EventPropertyPrimitive;

import java.net.URI;

@Deprecated
public class StreamBuilder {

	private SpDataStream stream;
	private EventSchema schema;

	private StreamBuilder(String name, String description, String uri)
	{
		stream = new SpDataStream();
		stream.setName(name);
		stream.setDescription(description);
		stream.setUri(uri);
	}

	private StreamBuilder(String uri)
	{
		stream = new SpDataStream();
		stream.setUri(uri);
		schema = new EventSchema();
	}

	public static StreamBuilder createStream(String name, String description, String uri)
	{
		return new StreamBuilder(name, description, uri);
	}

	public static StreamBuilder createStreamRestriction(String uri)
	{
		return new StreamBuilder(uri);
	}

	public StreamBuilder property(String propertyName, URI propertyType, URI subclassOf)
	{
		schema.addEventProperty(new EventPropertyPrimitive(propertyType.toString(), propertyName, "", Utils.createURI(subclassOf.toString())));
		return this;
	}

	public StreamBuilder icon(String iconUrl)
	{
		stream.setIconUrl(iconUrl);
		return this;
	}

	public StreamBuilder schema(EventSchema eventSchema)
	{
		stream.setEventSchema(eventSchema);
		return this;
	}

	public StreamBuilder grounding(EventGrounding eventGrounding)
	{
		stream.setEventGrounding(eventGrounding);
		return this;
	}

	public SpDataStream build()
	{
		return stream;
	}

}
