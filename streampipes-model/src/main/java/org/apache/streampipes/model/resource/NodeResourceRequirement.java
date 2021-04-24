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
package org.apache.streampipes.model.resource;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import io.fogsy.empire.annotations.RdfsClass;
import org.apache.streampipes.model.base.UnnamedStreamPipesEntity;
import org.apache.streampipes.model.shared.annotation.TsModel;
import org.apache.streampipes.vocabulary.StreamPipes;


import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@RdfsClass(StreamPipes.NODE_RESOURCE_REQUIREMENT)
@Entity
@TsModel
@JsonSubTypes({
        @JsonSubTypes.Type(Hardware.class),
})
public abstract class NodeResourceRequirement extends UnnamedStreamPipesEntity {

    private static final long serialVersionUID = -8700750792058131323L;
    protected static final String prefix = "urn:streampipes.org:nrr:";

    public NodeResourceRequirement() {
        super(prefix + UUID.randomUUID().toString());
    }

    public NodeResourceRequirement(NodeResourceRequirement other) {
        super(other);
    }
}
