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

import { DataLakeMeasure } from '../gen/streampipes-model';

export interface DataExplorerFieldCharacteristics {
    dimension: boolean;
    numeric: boolean;
    binary: boolean;
    semanticTypes: string[];
}

export interface DataExplorerField {
    runtimeName: string;
    aggregation?: string;
    measure: string;
    fullDbName: string;
    sourceIndex: number;
    fieldCharacteristics: DataExplorerFieldCharacteristics;
}

export interface FieldConfig {
    runtimeName: string;
    aggregations?: string[];
    alias?: string;
    selected: boolean;
    numeric: boolean;
}

export interface SelectedFilter {
    field?: DataExplorerField;
    operator: string;
    value: any;
}

export interface QueryConfig {
    selectedFilters: SelectedFilter[];
    fields?: FieldConfig[];
    groupBy?: FieldConfig[];
    limit?: number;
    page?: number;
    order?: 'ASC' | 'DESC';
    autoAggregate?: boolean;
    aggregationValue?: number;
    aggregationTimeUnit?: string;
    aggregationFunction?: string;
}

export interface SourceConfig {
    measureName: string;
    measure?: DataLakeMeasure;
    queryConfig: QueryConfig;
    queryType: 'raw' | 'aggregated' | 'single';
    sourceType: 'pipeline' | 'measurement';
}

export interface DataExplorerDataConfig {
    sourceConfigs: SourceConfig[];
    ignoreTooMuchDataWarning: boolean;
    ignoreMissingValues: boolean;
}

export type MissingValueBehaviour = 'ignore' | 'empty';
