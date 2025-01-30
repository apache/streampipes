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
    CorrelationChartVisConfig,
    CorrelationChartWidgetModel,
} from '../model/correlation-chart-widget.model';
import { DataExplorerField } from '@streampipes/platform-services';

@Component({
    selector: 'sp-data-explorer-correlation-chart-widget-config',
    templateUrl: './correlation-chart-widget-config.component.html',
})
export class CorrelationWidgetConfigComponent extends BaseWidgetConfig<
    CorrelationChartWidgetModel,
    CorrelationChartVisConfig
> {
    updateFirstField(selectedField: DataExplorerField) {
        this.currentlyConfiguredWidget.visualizationConfig.firstField =
            selectedField;
        this.triggerViewRefresh();
    }

    updateSecondField(selectedField: DataExplorerField) {
        this.currentlyConfiguredWidget.visualizationConfig.secondField =
            selectedField;
        this.triggerViewRefresh();
    }

    protected applyWidgetConfig(config: CorrelationChartVisConfig): void {
        config.firstField = this.fieldService.getSelectedField(
            config.firstField,
            this.fieldProvider.numericFields,
            () => this.fieldProvider.numericFields[0],
        );
        const secondFieldIndex =
            this.fieldProvider.numericFields.length > 1 ? 1 : 0;
        config.secondField = this.fieldService.getSelectedField(
            config.secondField,
            this.fieldProvider.numericFields,
            () => this.fieldProvider.numericFields[secondFieldIndex],
        );
    }

    protected requiredFieldsForChartPresent(): boolean {
        return this.fieldProvider.numericFields.length > 0;
    }
}
