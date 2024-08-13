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
import { WidgetConfigurationService } from '../../../../services/widget-configuration.service';
import { DataExplorerFieldProviderService } from '../../../../services/data-explorer-field-provider-service';
import { GaugeVisConfig, GaugeWidgetModel } from '../model/gauge-widget.model';
import { DataExplorerField } from '@streampipes/platform-services';

@Component({
    selector: 'sp-data-explorer-gauge-widget-config',
    templateUrl: './gauge-widget-config.component.html',
})
export class GaugeWidgetConfigComponent extends BaseWidgetConfig<
    GaugeWidgetModel,
    GaugeVisConfig
> {
    constructor(
        widgetConfigurationService: WidgetConfigurationService,
        fieldService: DataExplorerFieldProviderService,
    ) {
        super(widgetConfigurationService, fieldService);
    }

    setSelectedProperty(field: DataExplorerField) {
        this.currentlyConfiguredWidget.visualizationConfig.selectedProperty =
            field;
        this.triggerViewRefresh();
    }

    protected applyWidgetConfig(config: GaugeVisConfig): void {
        config.selectedProperty = this.fieldService.getSelectedField(
            config.selectedProperty,
            this.fieldProvider.numericFields,
            () => this.fieldProvider.numericFields[0],
        );
        config.min ??= 0;
        config.max ??= 100;
    }

    protected requiredFieldsForChartPresent(): boolean {
        return this.fieldProvider.numericFields.length > 0;
    }
}
