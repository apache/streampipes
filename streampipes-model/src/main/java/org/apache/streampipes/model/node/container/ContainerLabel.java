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
package org.apache.streampipes.model.node.container;


import io.fogsy.empire.annotations.RdfProperty;
import io.fogsy.empire.annotations.RdfsClass;
import org.apache.streampipes.model.base.UnnamedStreamPipesEntity;
import org.apache.streampipes.model.shared.annotation.TsModel;
import org.apache.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;
import java.util.Map;

@RdfsClass(StreamPipes.CONTAINER_LABEL)
@Entity
@TsModel
public class ContainerLabel extends UnnamedStreamPipesEntity  {

    @RdfProperty(StreamPipes.HAS_CONTAINER_LABEL_KEY)
    private String key;

    @RdfProperty(StreamPipes.HAS_CONTAINER_LABEL_VALUE)
    private String value;

    public ContainerLabel() {
        super();
    }

    public ContainerLabel(String key, String value) {
        super();
        this.key = key;
        this.value = value;
    }

    public ContainerLabel(UnnamedStreamPipesEntity other, String key, String value) {
        super(other);
        this.key = key;
        this.value = value;
    }

    public ContainerLabel(String elementId, String key, String value) {
        super(elementId);
        this.key = key;
        this.value = value;
    }

    public ContainerLabel(Map.Entry<String, String> entity) {
        this.key = entity.getKey();
        this.value = entity.getValue();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
