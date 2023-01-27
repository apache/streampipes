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

import { GridsterConfig } from 'angular-gridster2';
import { WidgetType } from '../registry/data-explorer-widgets';
import { DataExplorerField } from '@streampipes/platform-services';

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface IDataViewDashboardConfig extends GridsterConfig {}

export interface IWidget {
    id: string;
    label: string;
    componentClass: any;
}

export interface WidgetBaseAppearanceConfig {
    backgroundColor: string;
    textColor: string;
    widgetTitle: string;
}

export interface WidgetTypeChangeMessage {
    widgetId: string;
    newWidgetTypeId: string;
}

export interface RefreshMessage {
    widgetId: string;
    refreshData: boolean;
    refreshView: boolean;
}

export interface FieldProvider {
    primaryTimestampField?: DataExplorerField;
    allFields: DataExplorerField[];
    numericFields: DataExplorerField[];
    booleanFields: DataExplorerField[];
    dimensionFields: DataExplorerField[];
    nonNumericFields: DataExplorerField[];
}

export interface DataExplorerVisConfig {
    forType: WidgetType;
}
