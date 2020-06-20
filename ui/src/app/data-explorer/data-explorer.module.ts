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
import { OWL_DATE_TIME_FORMATS, OwlDateTimeModule, OwlNativeDateTimeModule } from '@danielmoncada/angular-datetime-picker';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { GridsterModule } from 'angular-gridster2';
import { PlotlyViaWindowModule } from 'angular-plotly.js';
import { DynamicModule } from 'ng-dynamic-component';
import { ColorPickerModule } from 'ngx-color-picker';
import { ConnectModule } from '../connect/connect.module';
import { DatalakeRestService } from '../core-services/datalake/datalake-rest.service';
import { SemanticTypeUtilsService } from '../core-services/semantic-type/semantic-type-utils.service';
import { SharedDatalakeRestService } from '../core-services/shared/shared-dashboard.service';
import { CoreUiModule } from '../core-ui/core-ui.module';
import { LabelingToolModule } from '../core-ui/linechart/labeling-tool/labeling-tool.module';
import { CustomMaterialModule } from '../CustomMaterial/custom-material.module';
import { ElementIconText } from '../services/get-element-icon-text.service';
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
import { DataExplorerAddVisualizationDialogComponent } from './dialogs/add-widget/data-explorer-add-visualization-dialog.component';
import { DataExplorerEditDataViewDialogComponent } from './dialogs/edit-dashboard/data-explorer-edit-data-view-dialog.component';
import { DataLakeService } from './services/data-lake.service';
import { DataViewDataExplorerService } from './services/data-view-data-explorer.service';
import { RefreshDashboardService } from './services/refresh-dashboard.service';
import { ResizeService } from './services/resize.service';
import { GroupConfigurationComponent } from './components/widgets/utils/group-configuration/group-configuration.component';

const dashboardWidgets = [

];

export const MY_NATIVE_FORMATS = {
  fullPickerInput: {year: 'numeric', month: 'numeric', day: 'numeric', hour: 'numeric', minute: 'numeric', hour12: false},
  datePickerInput: {year: 'numeric', month: 'numeric', day: 'numeric', hour12: false},
  timePickerInput: {hour: 'numeric', minute: 'numeric', hour12: false},
  monthYearLabel: {year: 'numeric', month: 'short', hour12: false},
  dateA11yLabel: {year: 'numeric', month: 'long', day: 'numeric', hour12: false},
  monthYearA11yLabel: {year: 'numeric', month: 'long', hour12: false}
};


@NgModule({
  imports: [
    CommonModule,
    MatTabsModule,
    DynamicModule.withComponents(
      dashboardWidgets
    ),
    FlexLayoutModule,
    GridsterModule,
    CommonModule,
    FlexLayoutModule,
    CustomMaterialModule,
    FormsModule,
    ColorPickerModule,
    MatGridListModule,
    ConnectModule,
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
    MatChipsModule,
    LabelingToolModule
  ],
  declarations: [
    DataExplorerComponent,
    DataExplorerDashboardGridComponent,
    DataExplorerDashboardOverviewComponent,
    DataExplorerDashboardPanelComponent,
    DataExplorerDashboardWidgetComponent,
    DataExplorerAddVisualizationDialogComponent,
    DataExplorerEditDataViewDialogComponent,
    TableWidgetComponent,
    ImageWidgetComponent,
    LineChartWidgetComponent,
    TimeRangeSelectorComponent,
    NoDataInDateRangeComponent,
    LoadDataSpinnerComponent,
    DataDownloadDialog,
    SelectPropertiesComponent,
    AggregateConfigurationComponent,
    GroupConfigurationComponent
  ],
  providers: [
    DatalakeRestService,
    SharedDatalakeRestService,
    DataViewDataExplorerService,
    DataLakeService,
    ResizeService,
    RefreshDashboardService,
    SemanticTypeUtilsService,
    {
      provide: 'RestApi',
      useFactory: ($injector: any) => $injector.get('RestApi'),
      deps: ['$injector']
    },
    ElementIconText,
    {
      provide: OWL_DATE_TIME_FORMATS, useValue: MY_NATIVE_FORMATS
    }
  ],
  exports: [
    DataExplorerComponent
  ],
  entryComponents: [
    DataExplorerComponent,
    DataExplorerAddVisualizationDialogComponent,
    DataDownloadDialog,
    DataExplorerEditDataViewDialogComponent
  ]
})
export class DataExplorerModule {

  constructor() {
  }
}
