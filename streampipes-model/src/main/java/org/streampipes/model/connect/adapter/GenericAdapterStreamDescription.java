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
import org.streampipes.model.connect.grounding.FormatDescription;
import org.streampipes.model.connect.grounding.ProtocolDescription;
import org.streampipes.model.connect.grounding.ProtocolStreamDescription;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@Namespaces({"sp", "https://streampipes.org/vocabulary/v1/"})
@RdfsClass(StreamPipes.GENERIC_ADAPTER_STREAM_DESCRIPTION)
@Entity
public class GenericAdapterStreamDescription extends AdapterStreamDescription implements  GenericAdapterDescription{
    public static final String ID = "http://streampipes.org/genericadapterstreamdescription";

    //private String sourceType = "org.streampipes.model.connect.adapter.GenericAdapterStreamDescription";

    @RdfProperty("sp:hasFormat")
    private FormatDescription formatDescription;

    @RdfProperty("sp:hasProtocol")
    private ProtocolDescription protocolDescription;

    public GenericAdapterStreamDescription() {
        super(ID, "GenericAdapterStreamDescription", "");
        setAdapterId(ID);
    }

    public GenericAdapterStreamDescription(FormatDescription formatDescription, ProtocolStreamDescription protocolDescription) {
        this.formatDescription = formatDescription;
        this.protocolDescription = protocolDescription;
    }

    public GenericAdapterStreamDescription(GenericAdapterStreamDescription other) {
        super(other);

        if (other.getFormatDescription() != null) this.formatDescription = new FormatDescription(other.getFormatDescription());
        if (other.getProtocolDescription() != null) this.protocolDescription = new ProtocolStreamDescription(other.getProtocolDescription());
    }

    public FormatDescription getFormatDescription() {
        return formatDescription;
    }

    public void setFormatDescription(FormatDescription formatDescription) {
        this.formatDescription = formatDescription;
    }

    public ProtocolDescription getProtocolDescription() {
        return protocolDescription;
    }

    public void setProtocolDescription(ProtocolDescription protocolDescription) {
        this.protocolDescription = protocolDescription;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }
}
