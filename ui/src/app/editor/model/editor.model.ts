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

import {
    DataProcessorInvocation,
    DataSinkInvocation,
    SpDataStream,
} from '@streampipes/platform-services';
import { InjectionToken } from '@angular/core';

export interface PipelineElementHolder {
    [key: string]: PipelineElementUnion[];
}

export interface PipelineElementPosition {
    x: number;
    y: number;
}

export enum PipelineElementConfigurationStatus {
    OK = 1,
    MODIFIED,
    INCOMPLETE,
}

export interface PipelineElementConfig {
    type: string;
    settings: {
        openCustomize: boolean;
        preview: boolean;
        displaySettings: string;
        connectable: string;
        disabled: boolean;
        loadingStatus: boolean;
        completed: PipelineElementConfigurationStatus;
        position: {
            x: number;
            y: number;
        };
    };
    payload: PipelineElementUnion;
}

export interface PipelineElementRecommendationLayout {
    skewStyle: any;
    unskewStyle: any;
    unskewStyleLabel: any;
    type: string;
}

export enum PipelineElementType {
    DataSet,
    DataStream,
    DataProcessor,
    DataSink,
}

export interface TabsModel {
    title: string;
    type: PipelineElementIdentifier;
    shorthand: string;
}

export type PipelineElementUnion =
    | SpDataStream
    | DataProcessorInvocation
    | DataSinkInvocation;

export type InvocablePipelineElementUnion =
    | DataProcessorInvocation
    | DataSinkInvocation;

export const PIPELINE_ELEMENT_TOKEN = new InjectionToken<{}>('pipelineElement');

export type PipelineElementIdentifier =
    | 'org.apache.streampipes.model.SpDataStream'
    | 'org.apache.streampipes.model.graph.DataProcessorInvocation'
    | 'org.apache.streampipes.model.graph.DataSinkInvocation';

export interface PeCategory {
    code: string;
    label: string;
    description: string;
}
