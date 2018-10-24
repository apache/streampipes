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

package org.streampipes.sdk;

import org.streampipes.commons.Utils;
import org.streampipes.model.schema.EventPropertyPrimitive;

import java.net.URI;

@Deprecated
public class PrimitivePropertyBuilder {

	private EventPropertyPrimitive primitive;
	
	private PrimitivePropertyBuilder(String dataType, String runtimeName, String subPropertyOf) {
		primitive = new EventPropertyPrimitive(dataType, runtimeName, "", Utils.createURI(subPropertyOf));
	}
	
	private PrimitivePropertyBuilder(String subPropertyOf) {
		primitive = new EventPropertyPrimitive(Utils.createURI(subPropertyOf));
	}
	
	public static PrimitivePropertyBuilder createProperty(URI dataType, String runtimeName, String subPropertyOf)
	{
		return new PrimitivePropertyBuilder(dataType.toString(), runtimeName, subPropertyOf);
	}
	
	public static PrimitivePropertyBuilder createPropertyRestriction(String subPropertyOf)
	{
		return new PrimitivePropertyBuilder("", "", subPropertyOf);
	}
	
	public PrimitivePropertyBuilder label(String label)
	{
		primitive.setLabel(label);
		return this;
	}
	
	public PrimitivePropertyBuilder description(String description)
	{
		return this;
	}
	
	public EventPropertyPrimitive build()
	{
		return primitive;
	}
	
}
