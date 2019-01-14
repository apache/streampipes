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

package org.streampipes.model.output;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.UnnamedStreamPipesEntity;
import org.streampipes.model.util.Cloner;
import org.streampipes.vocabulary.StreamPipes;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

@RdfsClass(StreamPipes.OUTPUT_STRATEGY)
@MappedSuperclass
@Entity
public abstract class OutputStrategy extends UnnamedStreamPipesEntity {

  private static final long serialVersionUID = 1953204905003864143L;

  @RdfProperty(StreamPipes.HAS_NAME)
  private String name;

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.HAS_RENAME_RULE)
  private List<PropertyRenameRule> renameRules;

  public OutputStrategy() {
    super();
    this.renameRules = new ArrayList<>();
  }

  public OutputStrategy(OutputStrategy other) {
    super(other);
    this.name = other.getName();
    this.renameRules = new Cloner().renameRules(other.getRenameRules());
  }

  public OutputStrategy(String name) {
    super();
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<PropertyRenameRule> getRenameRules() {
    return renameRules;
  }

  public void setRenameRules(List<PropertyRenameRule> renameRules) {
    this.renameRules = renameRules;
  }
}
