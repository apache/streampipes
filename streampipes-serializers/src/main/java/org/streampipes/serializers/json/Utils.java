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

package org.streampipes.serializers.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.streampipes.model.client.messages.Message;
import org.streampipes.model.client.ontology.Range;


public class Utils {

	public static Gson getGson()
	{
		GsonBuilder gsonBuilder = getGsonBuilder();
		Gson gson = gsonBuilder.create();
		return gson;
	}
	
	public static GsonBuilder getGsonBuilder()
	{
		GsonBuilder gsonBuilder = GsonSerializer.getGsonBuilder();
		gsonBuilder.registerTypeAdapter(Range.class, new RangeSerializer());
		gsonBuilder.registerTypeAdapter(Message.class, new JsonLdSerializer<Message>());
		return gsonBuilder;	
	}
	
}
