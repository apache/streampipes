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

package org.streampipes.model.staticproperty;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.UnnamedStreamPipesEntity;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@RdfsClass(StreamPipes.OPTION)
@Entity
public class Option extends UnnamedStreamPipesEntity {
	
	private static final long serialVersionUID = 8536995294188662931L;

	@RdfProperty(StreamPipes.HAS_NAME)
	private String name;
	
	@RdfProperty(StreamPipes.IS_SELECTED)
	private boolean selected;

	@RdfProperty(StreamPipes.INTERNAL_NAME)
	private String internalName;
	
	public Option()
	{
		super();
	}
	
	public Option(String name)
	{
		super();
		this.name = name;
	}

	public Option(String name, String internalName) {
		super();
		this.name = name;
		this.internalName = internalName;
	}
	
	public Option(String name, boolean selected)
	{
		super();
		this.name = name;
		this.selected = selected;
	}

	public Option(Option o) {
		super(o);
		this.name = o.getName();
		this.selected = o.isSelected();
		this.internalName = o.getInternalName();
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getInternalName() {
		return internalName;
	}

	public void setInternalName(String internalName) {
		this.internalName = internalName;
	}
}
