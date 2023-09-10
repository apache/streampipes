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
    DatasetOption,
    GridOption,
    XAXisOption,
    YAXisOption,
} from 'echarts/types/dist/shared';

export interface WidgetSize {
    width: number;
    height: number;
}

export interface TagValue {
    tagKeys: string[];
    values: any[];
}

export interface GeneratedDataset {
    dataset: DatasetOption[];
    tagValues: TagValue[][];
    rawDataStartIndices: number[];
    rawDataEndIndices: number[];
    preparedDataStartIndices: number[];
    preparedDataEndIndices: number[];
    initialTransformsCount: number;
}

export interface GridOptions {
    grid: GridOption[];
    numberOfRows: number;
    numberOfColumns: number;
}

export interface AxisOptions {
    xAxisOptions: XAXisOption[];
    yAxisOptions: YAXisOption[];
}
