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
import { BaseWidgetConfig } from '../../base/base-widget-config';
import {
    IndicatorChartVisConfig,
    IndicatorChartWidgetModel,
} from '../model/indicator-chart-widget.model';
import { DataExplorerField } from '@streampipes/platform-services';
import { WidgetType } from '../../../../registry/data-explorer-widgets';

@Component({
    selector: 'sp-data-explorer-indicator-chart-widget-config',
    templateUrl: './indicator-chart-widget-config.component.html',
})
export class IndicatorWidgetConfigComponent
    extends BaseWidgetConfig<IndicatorChartWidgetModel, IndicatorChartVisConfig>
    implements OnInit
{
    ngOnInit(): void {
        super.onInit();
    }

    updateValue(field: DataExplorerField) {
        this.currentlyConfiguredWidget.visualizationConfig.valueField = field;
        this.triggerDataRefresh();
    }

    updateDelta(field: DataExplorerField) {
        this.currentlyConfiguredWidget.visualizationConfig.deltaField = field;
        this.triggerDataRefresh();
    }

    protected getWidgetType(): WidgetType {
        return WidgetType.IndicatorChart;
    }

    protected initWidgetConfig(): IndicatorChartVisConfig {
        return {
            forType: this.getWidgetType(),
            valueField:
                this.fieldProvider.numericFields.length > 0
                    ? this.fieldProvider.numericFields[0]
                    : undefined,
            showDelta: false,
        };
    }
}
