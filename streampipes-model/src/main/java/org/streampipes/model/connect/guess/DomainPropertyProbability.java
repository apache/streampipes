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

package org.streampipes.model.connect.guess;

import org.streampipes.empire.annotations.Namespaces;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.UnnamedStreamPipesEntity;

import javax.persistence.Entity;

@Namespaces({"sp", "https://streampipes.org/vocabulary/v1/"})
@RdfsClass("sp:DomainPropertyProbability")
@Entity
public class DomainPropertyProbability extends UnnamedStreamPipesEntity {

    @RdfProperty("sp:domainProperty")
    private String domainProperty;


    @RdfProperty("sp:probability")
    private String probability;

    public DomainPropertyProbability() {
        super();
    }

    public DomainPropertyProbability(String domainProperty,  String probability) {
        this.domainProperty = domainProperty;
        this.probability = probability;
    }

    public String getDomainProperty() {
        return domainProperty;
    }

    public void setDomainProperty(String domainProperty) {
        this.domainProperty = domainProperty;
    }

    public String getProbability() {
        return probability;
    }

    public void setProbability(String probability) {
        this.probability = probability;
    }
}
