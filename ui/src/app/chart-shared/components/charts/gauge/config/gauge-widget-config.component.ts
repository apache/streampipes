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

import { Component, inject } from '@angular/core';
import { BaseWidgetConfig } from '../../base/base-widget-config';
import { ChartConfigurationService } from '../../../../services/chart-configuration.service';
import { ChartFieldProviderService } from '../../../../services/chart-field-provider.service';
import { GaugeVisConfig, GaugeWidgetModel } from '../model/gauge-widget.model';
import { DataExplorerField } from '@streampipes/platform-services';
import { SpVisualizationConfigOuterComponent } from '../../../chart-config/visualization-config-outer/visualization-config-outer.component';
import {
    FormFieldComponent,
    SplitSectionComponent,
} from '@streampipes/shared-ui';
import { SelectSinglePropertyConfigComponent } from '../../../chart-config/select-single-property-config/select-single-property-config.component';
import { MatFormField } from '@angular/material/form-field';
import { FlexDirective } from '@ngbracket/ngx-layout/flex';
import { MatInput } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-data-explorer-gauge-widget-config',
    templateUrl: './gauge-widget-config.component.html',
    imports: [
        SpVisualizationConfigOuterComponent,
        SplitSectionComponent,
        SelectSinglePropertyConfigComponent,
        FormFieldComponent,
        MatFormField,
        FlexDirective,
        MatInput,
        FormsModule,
        TranslatePipe,
    ],
})
export class GaugeWidgetConfigComponent extends BaseWidgetConfig<
    GaugeWidgetModel,
    GaugeVisConfig
> {
    constructor() {
        const widgetConfigurationService = inject(ChartConfigurationService);
        const fieldService = inject(ChartFieldProviderService);

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
