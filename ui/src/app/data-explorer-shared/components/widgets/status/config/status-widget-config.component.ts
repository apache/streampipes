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
import {
    StatusWidgetModel,
    StatusVisConfig,
} from '../model/status-widget.model';
import { DataExplorerFieldProviderService } from '../../../../services/data-explorer-field-provider-service';
import { DataExplorerField } from '@streampipes/platform-services';

@Component({
    selector: 'sp-data-explorer-status-widget-config',
    templateUrl: './status-widget-config.component.html',
    styleUrls: ['./status-widget-config.component.scss'],
})
export class StatusWidgetConfigComponent extends BaseWidgetConfig<
    StatusWidgetModel,
    StatusVisConfig
> {
    constructor(
        widgetConfigurationService: WidgetConfigurationService,
        fieldService: DataExplorerFieldProviderService,
    ) {
        super(widgetConfigurationService, fieldService);
    }

    selectDataType(selectedDataType: string): void {
        this.currentlyConfiguredWidget.visualizationConfig.selectedDataType =
            selectedDataType;
        this.triggerViewRefresh();
    }

    selectInterval(selectedInterval: number): void {
        this.currentlyConfiguredWidget.visualizationConfig.selectedInterval =
            selectedInterval;
        this.triggerViewRefresh();
    }

    showLastSeen(selectedLastSeen: boolean): void {
        this.currentlyConfiguredWidget.visualizationConfig.showLastSeen =
            selectedLastSeen;
        this.triggerViewRefresh();
    }

    selectBooleanFieldToObserve(
        selectedBooleanFieldToObserve: DataExplorerField,
    ): void {
        this.currentlyConfiguredWidget.visualizationConfig.selectedBooleanFieldToObserve =
            selectedBooleanFieldToObserve;
        this.triggerViewRefresh();
    }

    selectMappingGreenTrue(selectedMappingGreenTrue: boolean): void {
        this.currentlyConfiguredWidget.visualizationConfig.selectedMappingGreenTrue =
            selectedMappingGreenTrue;
        this.triggerViewRefresh();
    }

    protected applyWidgetConfig(config: StatusVisConfig): void {
        config.selectedBooleanFieldToObserve =
            this.fieldService.getSelectedField(
                config.selectedBooleanFieldToObserve,
                this.fieldProvider.allFields,
                () => this.fieldProvider.allFields[0],
            );
        this.currentlyConfiguredWidget.visualizationConfig.selectedInterval ??= 5;
        this.currentlyConfiguredWidget.visualizationConfig.selectedMappingGreenTrue ??=
            true;
    }
    protected requiredFieldsForChartPresent(): boolean {
        return true;
    }
}
