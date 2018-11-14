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

package org.streampipes.model.connect.grounding;

import org.streampipes.empire.annotations.Namespaces;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.staticproperty.StaticProperty;

import javax.persistence.Entity;
import java.util.List;

@Namespaces({"sp", "https://streampipes.org/vocabulary/v1/"})
@RdfsClass("sp:ProtocolStreamDescription")
@Entity
public class ProtocolStreamDescription extends ProtocolDescription {

    public ProtocolStreamDescription() {
    }

    public ProtocolStreamDescription(String uri, String name, String description) {
        super(uri, name, description);
    }

    public ProtocolStreamDescription(String uri, String name, String description, List<StaticProperty> config) {
        super(uri, name, description, config);
    }

    public ProtocolStreamDescription(ProtocolDescription other) {
        super(other);
    }
}
