/*
 * Copyright 2019 FZI Forschungszentrum Informatik
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
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@RdfsClass(StreamPipes.FILE_STATIC_PROPERTY)
@Entity
public class FileStaticProperty extends StaticProperty {

    private static final long serialVersionUID = 1L;

    @RdfProperty(StreamPipes.HAS_ENDPOINT_URL)
	private String endpointUrl;

    @RdfProperty(StreamPipes.HAS_LOCATION_PATH)
	private String locationPath;

    public FileStaticProperty() {
        super(StaticPropertyType.FileStaticProperty);
    }

    public FileStaticProperty(FileStaticProperty other) {
        super(other);
        this.endpointUrl = other.getEndpointUrl();
        this.locationPath = other.getLocationPath();
    }

    public FileStaticProperty(String internalName, String label, String description)
	{
		super(StaticPropertyType.FileStaticProperty, internalName, label, description);
	}

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public String getLocationPath() {
        return locationPath;
    }

    public void setLocationPath(String locationPath) {
        this.locationPath = locationPath;
    }
}
