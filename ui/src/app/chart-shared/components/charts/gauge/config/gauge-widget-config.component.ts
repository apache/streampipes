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
import { MatCheckbox } from '@angular/material/checkbox';

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
        MatCheckbox,
        FormsModule,
        TranslatePipe,
    ],
})
export class GaugeWidgetConfigComponent extends BaseWidgetConfig<
    GaugeWidgetModel,
    GaugeVisConfig
> {
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
        const defaultDisplayName =
            config.selectedProperty?.runtimeName ||
            config.selectedProperty?.fullDbName ||
            '';
        if (typeof config.displayName !== 'string') {
            config.displayName = defaultDisplayName;
        }
        if (!config.displayName?.trim()) {
            config.displayName = defaultDisplayName;
        }
        config.min ??= 0;
        config.max ??= 100;
        config.startAngle ??= 225;
        config.endAngle ??= -45;
        config.splitNumber ??= 10;
        config.showPointer ??= true;
        config.enableThresholdColors ??= false;
        config.thresholdColorLow ??= '#91cc75';
        config.thresholdColorMedium ??= '#fac858';
        config.thresholdColorHigh ??= '#ee6666';

        const range = Math.max(1, config.max - config.min);
        config.thresholdLow ??= config.min + range * 0.6;
        config.thresholdHigh ??= config.min + range * 0.8;
    }

    protected requiredFieldsForChartPresent(): boolean {
        return this.fieldProvider.numericFields.length > 0;
    }
}
