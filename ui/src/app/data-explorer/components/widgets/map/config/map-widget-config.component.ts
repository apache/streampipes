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
import { MapVisConfig, MapWidgetModel } from '../model/map-widget.model';
import { DataExplorerField } from '@streampipes/platform-services';

@Component({
    selector: 'sp-data-explorer-map-widget-config',
    templateUrl: './map-widget-config.component.html',
})
export class MapWidgetConfigComponent extends BaseWidgetConfig<
    MapWidgetModel,
    MapVisConfig
> {
    markerOrTrace: string[];
    markerType: string[];

    constructor(
        widgetConfigurationService: WidgetConfigurationService,
        fieldService: DataExplorerFieldProviderService,
    ) {
        super(widgetConfigurationService, fieldService);
    }

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

    setUseLastEventCoordinations(field: DataExplorerField) {
        this.currentlyConfiguredWidget.visualizationConfig.useLastEventCoordinates =
            field['checked'];
        this.triggerDataRefresh();
    }

    setSelectedToolTipContent(fields: DataExplorerField[]) {
        this.currentlyConfiguredWidget.visualizationConfig.selectedToolTipContent =
            fields;
        this.triggerDataRefresh();
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
