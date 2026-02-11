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
import { ChartConfigurationService } from '../../../../services/chart-configuration.service';
import { TableVisConfig, TableWidgetModel } from '../model/table-widget.model';
import { ChartFieldProviderService } from '../../../../services/chart-field-provider.service';
import { DataExplorerField } from '@streampipes/platform-services';
import { SpVisualizationConfigOuterComponent } from '../../../chart-config/visualization-config-outer/visualization-config-outer.component';
import { SelectMultiplePropertiesConfigComponent } from '../../../chart-config/select-multiple-properties-config/select-multiple-properties-config.component';
import { SplitSectionComponent } from '@streampipes/shared-ui';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { FlexDirective } from '@ngbracket/ngx-layout/flex';
import { MatInput } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-data-explorer-table-widget-config',
    templateUrl: './table-widget-config.component.html',
    styleUrls: ['./table-widget-config.component.scss'],
    imports: [
        SpVisualizationConfigOuterComponent,
        SelectMultiplePropertiesConfigComponent,
        SplitSectionComponent,
        MatFormField,
        FlexDirective,
        MatLabel,
        MatInput,
        FormsModule,
        TranslatePipe,
    ],
})
export class TableWidgetConfigComponent extends BaseWidgetConfig<
    TableWidgetModel,
    TableVisConfig
> {
    constructor(
        widgetConfigurationService: ChartConfigurationService,
        fieldService: ChartFieldProviderService,
    ) {
        super(widgetConfigurationService, fieldService);
    }

    onFilterChange(searchValue: string): void {
        this.currentlyConfiguredWidget.visualizationConfig.searchValue =
            searchValue.trim().toLowerCase();
        this.triggerViewRefresh();
    }

    setSelectedColumn(selectedColumns: DataExplorerField[]) {
        this.currentlyConfiguredWidget.visualizationConfig.selectedColumns =
            selectedColumns;
        this.triggerViewRefresh();
    }

    protected applyWidgetConfig(config: TableVisConfig): void {
        config.selectedColumns = this.fieldService.getSelectedFields(
            config.selectedColumns,
            this.fieldProvider.allFields,
            () => {
                return this.fieldProvider.allFields.length > 6
                    ? this.fieldProvider.allFields.slice(0, 5)
                    : this.fieldProvider.allFields;
            },
        );
        config.searchValue ??= '';
    }

    protected requiredFieldsForChartPresent(): boolean {
        return true;
    }
}
