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

package org.apache.streampipes.codegeneration.api;

import org.apache.streampipes.model.base.ConsumableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.client.deployment.DeploymentConfiguration;
import org.apache.streampipes.model.client.deployment.OutputType;
import org.apache.streampipes.model.client.deployment.RuntimeType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.codegeneration.flink.sepa.FlinkSepaCodeGenerator;

public abstract class CodeGenerator {
    protected ConsumableStreamPipesEntity element;
    protected DeploymentConfiguration config;

    public CodeGenerator(DeploymentConfiguration config, ConsumableStreamPipesEntity element) {
        this.config = config;
        this.element = element;
    }

    public static ImplementationCodeGenerator getCodeGenerator(DeploymentConfiguration config, NamedStreamPipesEntity element) {

       if (config.getOutputType() == OutputType.IMPLEMENTATION) {
            if (config.getRuntimeType() == RuntimeType.FLINK) {
                if (element instanceof DataProcessorDescription) {
                    return new FlinkSepaCodeGenerator(config, (DataProcessorDescription) element, isStandalone(config));
                }
            }
        } else if (config.getOutputType() == OutputType.DESCRIPTION) {
        		return new FlinkSepaCodeGenerator(config, (DataProcessorDescription) element, isStandalone(config));
    	}
    	return null;
	}

    private static boolean isStandalone(DeploymentConfiguration config) {
        return config.getOutputType() == OutputType.IMPLEMENTATION;
    }
}
