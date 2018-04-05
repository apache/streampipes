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
package org.streampipes.model;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.UnnamedStreamPipesEntity;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@RdfsClass(StreamPipes.DATA_STREAM_CONTAINER)
@Entity

public class SpDataStreamContainer extends UnnamedStreamPipesEntity {

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty("sp:list")
  private List<SpDataStream> list;

  public SpDataStreamContainer() {
      super();
      this.list = new ArrayList<>();
  }

  public SpDataStreamContainer(List<SpDataStream> dataStreams) {
    super();
    this.list = dataStreams;
  }

  public List<SpDataStream> getList() {
    return list;
  }

  public void setList(List<SpDataStream> list) {
    this.list = list;
  }
}
