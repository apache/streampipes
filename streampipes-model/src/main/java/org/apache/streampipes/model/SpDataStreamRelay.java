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
package org.apache.streampipes.model;

import io.fogsy.empire.annotations.RdfProperty;
import io.fogsy.empire.annotations.RdfsClass;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.vocabulary.StreamPipes;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

@RdfsClass(StreamPipes.DATA_STREAM_RELAY)
@Entity
public class SpDataStreamRelay extends NamedStreamPipesEntity {

    private static final long serialVersionUID = -4675162465357705480L;

    private static final String prefix = "urn:apache.org:relaystream:";

    @OneToOne(fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @RdfProperty(StreamPipes.HAS_GROUNDING)
    protected EventGrounding eventGrounding;

    public SpDataStreamRelay() {
        super(prefix + RandomStringUtils.randomAlphabetic(6));
    }

    public SpDataStreamRelay(NamedStreamPipesEntity other, EventGrounding eventGrounding) {
        super(other);
        this.eventGrounding = eventGrounding;
    }

    public SpDataStreamRelay(SpDataStreamRelay other) {
        super(other);
        if (other.getEventGrounding() != null) {
            this.eventGrounding = new EventGrounding(other.getEventGrounding());
        }
    }

    public SpDataStreamRelay(EventGrounding eventGrounding) {
        super(prefix + RandomStringUtils.randomAlphabetic(6));
        this.eventGrounding = eventGrounding;
    }

    public EventGrounding getEventGrounding() {
        return eventGrounding;
    }

    public void setEventGrounding(EventGrounding eventGrounding) {
        this.eventGrounding = eventGrounding;
    }

}
