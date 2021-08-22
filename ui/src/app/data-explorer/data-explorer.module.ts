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

import { CdkTableModule } from '@angular/cdk/table';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatChipsModule } from '@angular/material/chips';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSliderModule } from '@angular/material/slider';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTabsModule } from '@angular/material/tabs';
import {
  OWL_DATE_TIME_FORMATS,
  OwlDateTimeModule,
  OwlNativeDateTimeModule
} from '@danielmoncada/angular-datetime-picker';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { GridsterModule } from 'angular-gridster2';
import { PlotlyViaWindowModule } from 'angular-plotly.js';
import { ColorPickerModule } from 'ngx-color-picker';
import { SemanticTypeUtilsService } from '../core-services/semantic-type/semantic-type-utils.service';
import { SharedDatalakeRestService } from '../core-services/shared/shared-dashboard.service';
import { CoreUiModule } from '../core-ui/core-ui.module';
import { CustomMaterialModule } from '../CustomMaterial/custom-material.module';
import { DataDownloadDialog } from './components/datadownloadDialog/dataDownload.dialog';
import { DataExplorerDashboardGridComponent } from './components/grid/data-explorer-dashboard-grid.component';
import { DataExplorerDashboardOverviewComponent } from './components/overview/data-explorer-dashboard-overview.component';
import { DataExplorerDashboardPanelComponent } from './components/panel/data-explorer-dashboard-panel.component';
import { TimeRangeSelectorComponent } from './components/time-selector/timeRangeSelector.component';
import { DataExplorerDashboardWidgetComponent } from './components/widget/data-explorer-dashboard-widget.component';
import { ImageWidgetComponent } from './components/widgets/image/image-widget.component';
import { LineChartWidgetComponent } from './components/widgets/line-chart/line-chart-widget.component';
import { TableWidgetComponent } from './components/widgets/table/table-widget.component';
import { AggregateConfigurationComponent } from './components/widgets/utils/aggregate-configuration/aggregate-configuration.component';
import { LoadDataSpinnerComponent } from './components/widgets/utils/load-data-spinner/load-data-spinner.component';
import { NoDataInDateRangeComponent } from './components/widgets/utils/no-data/no-data-in-date-range.component';
import { SelectPropertiesComponent } from './components/widgets/utils/select-properties/select-properties.component';
import { DataExplorerComponent } from './data-explorer.component';
import { DataExplorerEditDataViewDialogComponent } from './dialogs/edit-dashboard/data-explorer-edit-data-view-dialog.component';
import { DataViewDataExplorerService } from './services/data-view-data-explorer.service';
import { RefreshDashboardService } from './services/refresh-dashboard.service';
import { ResizeService } from './services/resize.service';
import { GroupConfigurationComponent } from './components/widgets/utils/group-configuration/group-configuration.component';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { ColorService } from './components/widgets/line-chart/services/color.service';
import { DataExplorerDesignerPanelComponent } from './components/designer-panel/data-explorer-designer-panel.component';
import { TableWidgetConfigComponent } from './components/widgets/table/config/table-widget-config.component';
import { DataExplorerWidgetAppearanceSettingsComponent } from './components/designer-panel/appearance-settings/data-explorer-widget-appearance-settings.component';
import { DataExplorerWidgetDataSettingsComponent } from './components/designer-panel/data-settings/data-explorer-widget-data-settings.component';
import { WidgetConfigurationService } from './services/widget-configuration.service';
import { LineChartWidgetConfigComponent } from './components/widgets/line-chart/config/line-chart-widget-config.component';
import { ImageWidgetConfigComponent } from './components/widgets/image/config/image-widget-config.component';
import { DatalakeRestService } from '../platform-services/apis/datalake-rest.service';
import { IndicatorChartWidgetComponent } from './components/widgets/indicator/indicator-chart-widget.component';
import { IndicatorWidgetConfigComponent } from './components/widgets/indicator/config/indicator-chart-widget-config.component';

export const MY_NATIVE_FORMATS = {
  fullPickerInput: {
    year: 'numeric',
    month: 'numeric',
    day: 'numeric',
    hour: 'numeric',
    minute: 'numeric',
    hour12: false
  },
  datePickerInput: {year: 'numeric', month: 'numeric', day: 'numeric', hour12: false},
  timePickerInput: {hour: 'numeric', minute: 'numeric', hour12: false},
  monthYearLabel: {year: 'numeric', month: 'short', hour12: false},
  dateA11yLabel: {year: 'numeric', month: 'long', day: 'numeric', hour12: false},
  monthYearA11yLabel: {year: 'numeric', month: 'long', hour12: false}
};


@NgModule({
  imports: [
    CommonModule,
    CoreUiModule,
    MatTabsModule,
    GridsterModule,
    FlexLayoutModule,
    CustomMaterialModule,
    FormsModule,
    ColorPickerModule,
    MatGridListModule,
    NgxChartsModule,
    CdkTableModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    ReactiveFormsModule,
    CoreUiModule,
    OwlDateTimeModule,
    OwlNativeDateTimeModule,
    PlotlyViaWindowModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSliderModule,
    MatSlideToggleModule,
    MatChipsModule
  ],
  declarations: [
    DataExplorerComponent,
    DataExplorerDashboardGridComponent,
    DataExplorerDashboardOverviewComponent,
    DataExplorerDashboardPanelComponent,
    DataExplorerDashboardWidgetComponent,
    DataExplorerDesignerPanelComponent,
    DataExplorerEditDataViewDialogComponent,
    DataExplorerWidgetAppearanceSettingsComponent,
    DataExplorerWidgetDataSettingsComponent,
    TableWidgetComponent,
    ImageWidgetComponent,
    ImageWidgetConfigComponent,
    IndicatorChartWidgetComponent,
    IndicatorWidgetConfigComponent,
    LineChartWidgetComponent,
    LineChartWidgetConfigComponent,
    TimeRangeSelectorComponent,
    NoDataInDateRangeComponent,
    LoadDataSpinnerComponent,
    DataDownloadDialog,
    SelectPropertiesComponent,
    AggregateConfigurationComponent,
    GroupConfigurationComponent,
    TableWidgetConfigComponent
  ],
  providers: [
    DatalakeRestService,
    SharedDatalakeRestService,
    DataViewDataExplorerService,
    ResizeService,
    ColorService,
    RefreshDashboardService,
    SemanticTypeUtilsService,
    WidgetConfigurationService,
    {
      provide: OWL_DATE_TIME_FORMATS, useValue: MY_NATIVE_FORMATS
    }
  ],
  exports: [
    DataExplorerComponent
  ],
  entryComponents: [
    DataExplorerComponent,
    DataDownloadDialog,
    DataExplorerEditDataViewDialogComponent
  ]
})
export class DataExplorerModule {

  constructor() {
  }
}
