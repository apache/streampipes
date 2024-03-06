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
    GridsterConfig,
    GridsterItem,
    GridsterItemComponent,
} from 'angular-gridster2';
import {
    DashboardItem,
    DataExplorerField,
    DataExplorerWidgetModel,
    SpLogMessage,
    SpQueryResult,
    TimeSettings,
} from '@streampipes/platform-services';
import { EChartsOption } from 'echarts';
import { WidgetSize } from './dataset.model';
import { EventEmitter } from '@angular/core';
import { FieldUpdateInfo } from './field-update.model';

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface IDataViewDashboardConfig extends GridsterConfig {}

export interface BaseWidgetData<T extends DataExplorerWidgetModel> {
    removeWidgetCallback: EventEmitter<boolean>;
    timerCallback: EventEmitter<boolean>;
    errorCallback: EventEmitter<SpLogMessage>;

    gridsterItem: GridsterItem;
    gridsterItemComponent: GridsterItemComponent;
    editMode: boolean;

    timeSettings: TimeSettings;

    dataViewDashboardItem: DashboardItem;
    dataExplorerWidget: T;
    previewMode: boolean;
    gridMode: boolean;

    cleanupSubscriptions(): void;
}

export interface SpEchartsRenderer<T extends DataExplorerWidgetModel> {
    render(
        queryResult: SpQueryResult[],
        widgetConfig: T,
        widgetSize: WidgetSize,
    ): EChartsOption;

    handleUpdatedFields(
        fieldUpdateInfo: FieldUpdateInfo,
        widgetConfig: T,
    ): void;
}

export interface IWidget<T extends DataExplorerWidgetModel> {
    id: string;
    label: string;
    widgetComponent: any;
    widgetConfigurationComponent?: any;
    chartRenderer?: SpEchartsRenderer<T>;
    alias?: string;
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
    forType?: number | string;
    configurationValid: boolean;
}
