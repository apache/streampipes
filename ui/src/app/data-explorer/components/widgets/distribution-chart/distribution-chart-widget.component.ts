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

import { Component, OnInit } from '@angular/core';
import { DistributionChartWidgetModel } from './model/distribution-chart-widget.model';
import { DataExplorerField } from '@streampipes/platform-services';
import { BaseDataExplorerEchartsWidgetDirective } from '../base/base-data-explorer-echarts-widget.directive';
import { SpEchartsRenderer } from '../../../models/dataview-dashboard.model';
import { DataExplorerWidgetRegistry } from '../../../registry/data-explorer-widget-registry';
import { WidgetType } from '../../../registry/data-explorer-widgets';

@Component({
    selector: 'sp-data-explorer-distribution-chart-widget',
    templateUrl: '../base/echarts-widget.component.html',
    styleUrls: ['../base/echarts-widget.component.scss'],
})
export class DistributionChartWidgetComponent
    extends BaseDataExplorerEchartsWidgetDirective<DistributionChartWidgetModel>
    implements OnInit
{
    handleUpdatedFields(
        addedFields: DataExplorerField[],
        removedFields: DataExplorerField[],
    ) {
        this.dataExplorerWidget.visualizationConfig.selectedProperty =
            this.triggerFieldUpdate(
                this.dataExplorerWidget.visualizationConfig.selectedProperty,
                addedFields,
                removedFields,
            );
    }

    getRenderer(): SpEchartsRenderer<DistributionChartWidgetModel> {
        const widgetCategory = DataExplorerWidgetRegistry.getWidgetTemplate(
            WidgetType.DistributionChart,
        );
        const chartType =
            this.dataExplorerWidget.visualizationConfig.displayType;
        return widgetCategory.renderers.find(r => r.getType() === chartType);
    }
}
