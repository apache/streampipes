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

package org.apache.streampipes.model.base;


import org.apache.streampipes.model.shared.annotation.TsModel;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;


/**
 * top-level StreamPipes element
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
@TsModel
public class AbstractStreamPipesEntity implements Serializable {

  private static final long serialVersionUID = -8593749314663582071L;

  protected @SerializedName("_id") String elementId;

  AbstractStreamPipesEntity() {

  }

  AbstractStreamPipesEntity(AbstractStreamPipesEntity other) {
    this.elementId = other.getElementId();
  }

  public String getElementId() {
    return elementId;
  }

  public void setElementId(String elementId) {
    this.elementId = elementId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AbstractStreamPipesEntity that = (AbstractStreamPipesEntity) o;
    return Objects.equals(elementId, that.elementId);
  }

}
