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

#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )

package ${package}.pe.processor.${packageName};

import ${package}.config.Config;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorRuntime;
import org.apache.streampipes.wrapper.flink.FlinkDeploymentConfig;

import java.io.Serializable;

public class ${classNamePrefix}Program extends
        FlinkDataProcessorRuntime<${classNamePrefix}Parameters>
implements Serializable {

  private static final long serialVersionUID = 1L;

  public ${classNamePrefix}Program(${classNamePrefix}Parameters params, boolean debug) {
    super(params, debug);
  }

  @Override
  protected FlinkDeploymentConfig getDeploymentConfig() {
    return new FlinkDeploymentConfig(Config.JAR_FILE,
            Config.INSTANCE.getFlinkHost(), Config.INSTANCE.getFlinkPort());
  }

  @Override
  protected DataStream<Event> getApplicationLogic(
        DataStream<Event>... messageStream) {

    return messageStream[0]
        .flatMap(new ${classNamePrefix}());
  }
}
