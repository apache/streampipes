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
    PieChartVisConfig,
    PieChartWidgetModel,
} from '../model/pie-chart-widget.model';
import { DataExplorerField } from '@streampipes/platform-services';
import { SpVisualizationConfigOuterComponent } from '../../../chart-config/visualization-config-outer/visualization-config-outer.component';
import {
    FormFieldComponent,
    SplitSectionComponent,
} from '@streampipes/shared-ui';
import { SelectSinglePropertyConfigComponent } from '../../../chart-config/select-single-property-config/select-single-property-config.component';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatOption, MatSelect } from '@angular/material/select';
import { MatSlider, MatSliderThumb } from '@angular/material/slider';
import { FormsModule } from '@angular/forms';
import { ColorMappingOptionsConfigComponent } from '../../../chart-config/color-mapping-options-config/color-mapping-options-config.component';
import { TranslatePipe } from '@ngx-translate/core';
import { MatCheckbox } from '@angular/material/checkbox';

@Component({
    selector: 'sp-pie-chart-widget-config',
    templateUrl: './pie-chart-widget-config.component.html',
    imports: [
        SpVisualizationConfigOuterComponent,
        SplitSectionComponent,
        SelectSinglePropertyConfigComponent,
        FormFieldComponent,
        MatFormField,
        MatInput,
        MatSelect,
        MatOption,
        MatSlider,
        MatSliderThumb,
        MatCheckbox,
        FormsModule,
        ColorMappingOptionsConfigComponent,
        TranslatePipe,
    ],
})
export class SpPieChartWidgetConfigComponent extends BaseWidgetConfig<
    PieChartWidgetModel,
    PieChartVisConfig
> {
    setSelectedProperty(field: DataExplorerField) {
        this.currentlyConfiguredWidget.visualizationConfig.selectedProperty =
            field;
        this.triggerViewRefresh();
    }

    protected applyWidgetConfig(config: PieChartVisConfig): void {
        config.selectedProperty = this.fieldService.getSelectedField(
            config.selectedProperty,
            this.fieldProvider.allFields,
            () => this.fieldProvider.allFields[0],
        );
        config.roundingValue ??= 0.1;
        config.selectedRadius ??= 0;
        config.startAngle ??= 90;
        config.clockwise ??= true;
        config.minAngle ??= 0;
        config.labelMode ??= 'name_percent';
        config.labelPosition ??= 'outside';
        config.labelAlignTo ??= 'edge';
        config.avoidLabelOverlap ??= true;
        config.showLabelLine ??= true;
        config.topNEnabled ??= false;
        config.topN ??= 10;
        config.othersLabel ??= 'Others';
        config.colorMappingsPieChart ??= [];
        config.showCustomColorMappingPieChart ??= false;
    }

    updateRoundingValue(selectedType: number) {
        this.currentlyConfiguredWidget.visualizationConfig.roundingValue =
            selectedType;
        this.triggerViewRefresh();
    }

    updateInnerRadius(selectedRadius: number) {
        this.currentlyConfiguredWidget.visualizationConfig.selectedRadius =
            selectedRadius;
        this.triggerViewRefresh();
    }

    protected requiredFieldsForChartPresent(): boolean {
        return this.fieldProvider.allFields.length > 0;
    }

    triggerViewUpdate() {
        this.widgetConfigurationService.notify({
            refreshView: true,
            refreshData: false,
        });
    }
}
