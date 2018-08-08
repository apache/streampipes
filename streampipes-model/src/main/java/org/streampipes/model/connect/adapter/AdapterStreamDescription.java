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

package org.streampipes.model.connect.adapter;
import org.streampipes.empire.annotations.Namespaces;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.connect.grounding.FormatDescription;
import org.streampipes.model.connect.grounding.ProtocolDescription;

import javax.persistence.Entity;

@Namespaces({"sp", "https://streampipes.org/vocabulary/v1/"})
@RdfsClass("sp:AdapterStreamDescription")
@Entity
public class AdapterStreamDescription extends AdapterDescription {

    public AdapterStreamDescription() {
    }

    public AdapterStreamDescription(AdapterStreamDescription other) {
        super(other);
        if (other.getDataStream() != null) this.setDataStream(new SpDataStream(other.getDataStream()));
    }

    @RdfProperty("sp:hasDataStream")
    private SpDataStream dataStream;

    public SpDataStream getDataStream() {
        return dataStream;
    }

    public void setDataStream(SpDataStream dataStream) {
        this.dataStream = dataStream;
    }
}
