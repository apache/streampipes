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
import { MapVisConfig, MapWidgetModel } from '../model/map-widget.model';
import { DataExplorerField } from '@streampipes/platform-services';
import { MatCheckbox, MatCheckboxChange } from '@angular/material/checkbox';
import { SpVisualizationConfigOuterComponent } from '../../../chart-config/visualization-config-outer/visualization-config-outer.component';
import {
    DefaultFlexDirective,
    DefaultLayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import {
    FormFieldComponent,
    SplitSectionComponent,
} from '@streampipes/shared-ui';
import { SelectSinglePropertyConfigComponent } from '../../../chart-config/select-single-property-config/select-single-property-config.component';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { MatOption, MatSelect } from '@angular/material/select';
import { SelectMultiplePropertiesConfigComponent } from '../../../chart-config/select-multiple-properties-config/select-multiple-properties-config.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-data-explorer-map-widget-config',
    templateUrl: './map-widget-config.component.html',
    imports: [
        SpVisualizationConfigOuterComponent,
        DefaultLayoutDirective,
        SplitSectionComponent,
        FormFieldComponent,
        SelectSinglePropertyConfigComponent,
        DefaultFlexDirective,
        MatFormField,
        MatInput,
        FormsModule,
        MatCheckbox,
        MatSelect,
        MatOption,
        SelectMultiplePropertiesConfigComponent,
        TranslatePipe,
    ],
})
export class MapWidgetConfigComponent extends BaseWidgetConfig<
    MapWidgetModel,
    MapVisConfig
> {
    markerOrTrace: string[];
    markerType: string[];

    setSelectedLongitudeProperty(field: DataExplorerField) {
        this.currentlyConfiguredWidget.visualizationConfig.selectedLongitudeProperty =
            field;
        this.triggerDataRefresh();
    }

    setSelectedLatitudeProperty(field: DataExplorerField) {
        this.currentlyConfiguredWidget.visualizationConfig.selectedLatitudeProperty =
            field;
        this.triggerDataRefresh();
    }

    setZoomValue(field: string) {
        const fieldToNumber: number = +field;
        this.currentlyConfiguredWidget.visualizationConfig.selectedZoomValue =
            fieldToNumber;
        this.triggerDataRefresh();
    }

    setUseLastEventCoordinates(event: MatCheckboxChange) {
        this.currentlyConfiguredWidget.visualizationConfig.useLastEventCoordinates =
            event.checked;
        this.triggerViewRefresh();
    }

    setSelectedToolTipContent(fields: DataExplorerField[]) {
        this.currentlyConfiguredWidget.visualizationConfig.selectedToolTipContent =
            fields;
        this.triggerViewRefresh();
    }

    protected applyWidgetConfig(config: MapVisConfig): void {
        this.markerOrTrace = ['marker', 'trace'];
        this.markerType = ['pin', 'car'];

        config.selectedLatitudeProperty = this.selectField(
            config.selectedLatitudeProperty,
            0,
        );
        config.selectedLongitudeProperty = this.selectField(
            config.selectedLongitudeProperty,
            1,
        );
        config.selectedToolTipContent = this.fieldProvider.allFields;
        config.selectedMarkerOrTrace ??= this.markerOrTrace[0];
        config.selectedMarkerType ??= this.markerType[0];
        config.selectedZoomValue ??= 1;
        config.useLastEventCoordinates ??= true;
    }

    selectField(field: DataExplorerField, index: number): DataExplorerField {
        return this.fieldService.getSelectedField(
            field,
            this.fieldProvider.numericFields,
            () => this.fieldProvider.numericFields[index],
        );
    }

    protected requiredFieldsForChartPresent(): boolean {
        return this.fieldProvider.numericFields.length > 1;
    }
}
