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
import org.streampipes.model.util.Cloner;
import org.streampipes.vocabulary.StreamPipes;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@RdfsClass(StreamPipes.COLLECTION_STATIC_PROPERTY)
@Entity
public class CollectionStaticProperty extends StaticProperty {

	private static final long serialVersionUID = 1L;

	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty(StreamPipes.MEMBER)
	private List<StaticProperty> members;
	
	@RdfProperty(StreamPipes.MEMBER_TYPE)
	private String memberType;

	public CollectionStaticProperty() {
		super(StaticPropertyType.CollectionStaticProperty);
	}

	public CollectionStaticProperty(String internalName, String label, String description, List<StaticProperty> members, String memberType) {
		super(StaticPropertyType.CollectionStaticProperty, internalName, label, description);
		this.members = members;
		this.memberType = memberType;
	}

	public CollectionStaticProperty(CollectionStaticProperty other) {
		super(other);
		this.members = new Cloner().staticProperties(other.getMembers());
		this.memberType = other.getMemberType();
	}

	public List<StaticProperty> getMembers() {
		return members;
	}

	public void setMembers(List<StaticProperty> members) {
		this.members = members;
	}

	public String getMemberType() {
		return memberType;
	}

	public void setMemberType(String memberType) {
		this.memberType = memberType;
	}
}
