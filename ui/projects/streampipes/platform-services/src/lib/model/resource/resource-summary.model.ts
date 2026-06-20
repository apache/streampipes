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

import { PipelineHealthStatus } from '../gen/streampipes-model';
import { DataExplorerWidgetHealthStatus } from '../gen/streampipes-model';
export interface ResourceSummaryDto<T> {
    totalCount: number;
    resources: T[];
}

export interface DashboardSummaryDto {
    elementId: string;
    name: string;
    description: string;
    createdAtEpochMs: number | null;
    lastModifiedEpochMs: number | null;
}

export interface AdapterSummaryDto {
    elementId: string;
    correspondingDataStreamElementId: string;
    name: string;
    description: string;
    running: boolean;
    createdAt: number;
    appId: string;
    includedAssets: string[];
    icon: string;
}

export interface AssetSummaryDto {
    elementId: string;
    assetName: string;
    assetDescription: string;
    removable: boolean;
}

export interface DatasetSummaryDto {
    elementId: string;
    measureName: string;
    retentionConfigured: boolean;
    lastExport: string | null;
    lastRetentionStatus: boolean | null;
    pipelines: string[];
    removable: boolean;
}

export interface PipelineSummaryDto {
    elementId: string;
    name: string;
    description: string;
    createdAt: number;
    running: boolean;
    healthStatus: PipelineHealthStatus;
    pipelineNotifications: string[];
    valid: true;
}

export interface ChartSummaryDto {
    elementId: string;
    name: string;
    datasetName: string | null;
    createdAtEpochMs: number | null;
    lastModifiedEpochMs: number | null;
    multiSourceChart: boolean;
    widgetType: string;
    healthStatus: DataExplorerWidgetHealthStatus;
}
