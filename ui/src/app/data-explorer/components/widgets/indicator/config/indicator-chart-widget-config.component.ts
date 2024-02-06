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

import { Component } from '@angular/core';
import { BaseWidgetConfig } from '../../base/base-widget-config';
import {
    IndicatorChartVisConfig,
    IndicatorChartWidgetModel,
} from '../model/indicator-chart-widget.model';
import { DataExplorerField } from '@streampipes/platform-services';
import { MatCheckboxChange } from '@angular/material/checkbox';

@Component({
    selector: 'sp-data-explorer-indicator-chart-widget-config',
    templateUrl: './indicator-chart-widget-config.component.html',
})
export class IndicatorWidgetConfigComponent extends BaseWidgetConfig<
    IndicatorChartWidgetModel,
    IndicatorChartVisConfig
> {
    updateValue(field: DataExplorerField) {
        this.currentlyConfiguredWidget.visualizationConfig.valueField = field;
        this.triggerViewRefresh();
    }

    updateDelta(event: MatCheckboxChange) {
        this.triggerViewRefresh();
    }

    protected applyWidgetConfig(config: IndicatorChartVisConfig): void {
        config.valueField = this.fieldService.getSelectedField(
            config.valueField,
            this.fieldProvider.allFields,
            () => {
                return this.fieldProvider.allFields.length > 0
                    ? this.fieldProvider.allFields[0]
                    : undefined;
            },
        );
        config.showDelta ??= false;
    }

    protected requiredFieldsForChartPresent(): boolean {
        return this.fieldProvider.allFields.length > 0;
    }
}
