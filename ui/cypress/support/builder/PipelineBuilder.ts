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

import { PipelineInput } from '../model/PipelineInput';
import { PipelineElementInput } from '../model/PipelineElementInput';

export class PipelineBuilder {
    pipeline: PipelineInput;

    constructor(name: string) {
        this.pipeline = new PipelineInput();
        this.pipeline.pipelineName = name;
    }

    public static create(name: string) {
        return new PipelineBuilder(name);
    }

    public addSource(source: string) {
        this.pipeline.dataSource = source;

        return this;
    }

    public addSourceType(sourceType: string) {
        this.pipeline.dataSourceType = sourceType;

        return this;
    }

    public addProcessingElement(processingElement: PipelineElementInput) {
        this.pipeline.processingElement = processingElement;

        return this;
    }

    public addSink(sink: PipelineElementInput) {
        this.pipeline.dataSink = sink;

        return this;
    }

    build() {
        return this.pipeline;
    }
}
