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
package org.apache.streampipes.container.api;

import org.apache.streampipes.container.declarer.PipelineTemplateDeclarer;
import org.apache.streampipes.container.init.DeclarersSingleton;

import javax.ws.rs.Path;

import java.util.Map;

@Path("/template")
public class PipelineTemplateResource extends AbstractPipelineElementResource<PipelineTemplateDeclarer> {

  @Override
  protected Map<String, PipelineTemplateDeclarer> getElementDeclarers() {
    return DeclarersSingleton.getInstance().getPipelineTemplateDeclarers();
  }
}
