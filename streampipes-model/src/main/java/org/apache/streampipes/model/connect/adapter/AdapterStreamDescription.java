/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.model.connect.adapter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import io.fogsy.empire.annotations.Namespaces;
import io.fogsy.empire.annotations.RdfProperty;
import io.fogsy.empire.annotations.RdfsClass;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@Namespaces({"sp", "https://streampipes.org/vocabulary/v1/"})
@RdfsClass(StreamPipes.ADAPTER_STREAM_DESCRIPTION)
@Entity
@JsonSubTypes({
        @JsonSubTypes.Type(SpecificAdapterStreamDescription.class),
        @JsonSubTypes.Type(SpecificAdapterStreamDescription.class)
})
public abstract class AdapterStreamDescription extends AdapterDescription {

    @RdfProperty("sp:hasDataStream")
    private SpDataStream dataStream;

    private boolean running;

    public AdapterStreamDescription() {
        super();
    }

    public AdapterStreamDescription(String uri, String name, String description) {
        super(uri, name, description);
        setAdapterId(uri);
    }

    public AdapterStreamDescription(AdapterStreamDescription other) {
        super(other);
        this.running = other.isRunning();
        if (other.getDataStream() != null) {
            this.dataStream = new SpDataStream(other.getDataStream());
        }
    }

    public SpDataStream getDataStream() {
        return dataStream;
    }

    public void setDataStream(SpDataStream dataStream) {
        this.dataStream = dataStream;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
