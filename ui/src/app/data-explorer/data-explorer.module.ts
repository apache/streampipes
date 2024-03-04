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
import { FlexLayoutModule } from '@ngbracket/ngx-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatChipsModule } from '@angular/material/chips';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSliderModule } from '@angular/material/slider';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTabsModule } from '@angular/material/tabs';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';

import { NgxChartsModule } from '@swimlane/ngx-charts';
import { GridsterModule } from 'angular-gridster2';
import { ColorPickerModule } from 'ngx-color-picker';
import { SemanticTypeUtilsService } from '../core-services/semantic-type/semantic-type-utils.service';
import { PlatformServicesModule } from '@streampipes/platform-services';
import { CoreUiModule } from '../core-ui/core-ui.module';
import { CustomMaterialModule } from '../CustomMaterial/custom-material.module';
import { DataExplorerDashboardGridComponent } from './components/widget-view/grid-view/data-explorer-dashboard-grid.component';
import { DataExplorerDashboardOverviewComponent } from './components/overview/data-explorer-dashboard-overview.component';
import { DataExplorerDashboardPanelComponent } from './components/panel/data-explorer-dashboard-panel.component';
import { TimeRangeSelectorComponent } from './components/time-selector/timeRangeSelector.component';
import { DataExplorerDashboardWidgetComponent } from './components/widget/data-explorer-dashboard-widget.component';
import { ImageWidgetComponent } from './components/widgets/image/image-widget.component';
import { TableWidgetComponent } from './components/widgets/table/table-widget.component';
import { AggregateConfigurationComponent } from './components/widgets/utils/aggregate-configuration/aggregate-configuration.component';
import { LoadDataSpinnerComponent } from './components/widgets/utils/load-data-spinner/load-data-spinner.component';
import { NoDataInDateRangeComponent } from './components/widgets/utils/no-data/no-data-in-date-range.component';
import { SelectPropertiesComponent } from './components/widgets/utils/select-properties/select-properties.component';
import { SelectColorPropertiesComponent } from './components/widgets/utils/select-color-properties/select-color-properties.component';
import { DataExplorerEditDataViewDialogComponent } from './dialogs/edit-dashboard/data-explorer-edit-data-view-dialog.component';
import { GroupConfigurationComponent } from './components/widgets/utils/group-configuration/group-configuration.component';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { DataExplorerDesignerPanelComponent } from './components/designer-panel/data-explorer-designer-panel.component';
import { TableWidgetConfigComponent } from './components/widgets/table/config/table-widget-config.component';
import { MapWidgetComponent } from './components/widgets/map/map-widget.component';
import { MapWidgetConfigComponent } from './components/widgets/map/config/map-widget-config.component';
import { HeatmapWidgetConfigComponent } from './components/widgets/heatmap/config/heatmap-widget-config.component';
import { DataExplorerWidgetAppearanceSettingsComponent } from './components/designer-panel/appearance-settings/data-explorer-widget-appearance-settings.component';
import { DataExplorerWidgetDataSettingsComponent } from './components/designer-panel/data-settings/data-explorer-widget-data-settings.component';
import { TimeSeriesChartWidgetConfigComponent } from './components/widgets/time-series-chart/config/time-series-chart-widget-config.component';
import { ImageWidgetConfigComponent } from './components/widgets/image/config/image-widget-config.component';
import { IndicatorWidgetConfigComponent } from './components/widgets/indicator/config/indicator-chart-widget-config.component';
import { FieldSelectionPanelComponent } from './components/designer-panel/data-settings/field-selection-panel/field-selection-panel.component';
import { FieldSelectionComponent } from './components/designer-panel/data-settings/field-selection/field-selection.component';
import { FilterSelectionPanelComponent } from './components/designer-panel/data-settings/filter-selection-panel/filter-selection-panel.component';
import { SelectPropertyComponent } from './components/widgets/utils/select-property/select-property.component';
import { DataExplorerVisualisationSettingsComponent } from './components/designer-panel/visualisation-settings/data-explorer-visualisation-settings.component';
import { GroupSelectionPanelComponent } from './components/designer-panel/data-settings/group-selection-panel/group-selection-panel.component';
import { WidgetDirective } from './components/widget/widget.directive';
import { CorrelationWidgetConfigComponent } from './components/widgets/correlation-chart/config/correlation-chart-widget-config.component';
import { TooMuchDataComponent } from './components/widgets/utils/too-much-data/too-much-data.component';
import { RouterModule } from '@angular/router';
import { DataExplorerDashboardSlideViewComponent } from './components/widget-view/slide-view/data-explorer-dashboard-slide-view.component';
import { SharedUiModule } from '@streampipes/shared-ui';
import { DataExplorerPanelCanDeactivateGuard } from './data-explorer-panel.can-deactivate.guard';
import { NgxEchartsModule } from 'ngx-echarts';
import { ImageViewerComponent } from './components/widgets/image/image-viewer/image-viewer.component';
import { ImageBarComponent } from './components/widgets/image/image-bar/image-bar.component';
import { ImageBarPreviewComponent } from './components/widgets/image/image-bar/image-bar-preview/image-bar-preview.component';
import { SpEchartsWidgetComponent } from './components/widgets/base/echarts-widget.component';
import { SpValueHeatmapWidgetConfigComponent } from './components/widgets/value-heatmap/config/value-heatmap-chart-widget-config.component';
import { SpHistogramChartWidgetConfigComponent } from './components/widgets/histogram/config/histogram-chart-widget-config.component';
import { SpPieChartWidgetConfigComponent } from './components/widgets/pie/config/pie-chart-widget-config.component';
import { SpInvalidConfigurationComponent } from './components/widgets/utils/invalid-configuration/invalid-configuration.component';
import { SpConfigurationBoxComponent } from './components/widgets/utils/layout/configuration-box.component';
import { SpVisualizationConfigOuterComponent } from './components/widgets/utils/visualization-config-outer/visualization-config-outer.component';

@NgModule({
    imports: [
        CommonModule,
        LeafletModule,
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
        MatDatepickerModule,
        MatNativeDateModule,
        MatSliderModule,
        MatSlideToggleModule,
        MatChipsModule,
        PlatformServicesModule,
        SharedUiModule,
        NgxEchartsModule.forChild(),
        RouterModule.forChild([
            {
                path: 'dataexplorer',
                children: [
                    {
                        path: '',
                        component: DataExplorerDashboardOverviewComponent,
                    },
                    {
                        path: ':id',
                        component: DataExplorerDashboardPanelComponent,
                        canDeactivate: [DataExplorerPanelCanDeactivateGuard],
                    },
                    {
                        path: ':id/:startTime/:endTime',
                        component: DataExplorerDashboardPanelComponent,
                        canDeactivate: [DataExplorerPanelCanDeactivateGuard],
                    },
                ],
            },
        ]),
    ],
    declarations: [
        AggregateConfigurationComponent,
        DataExplorerDashboardGridComponent,
        DataExplorerDashboardOverviewComponent,
        DataExplorerDashboardPanelComponent,
        DataExplorerDashboardSlideViewComponent,
        DataExplorerDashboardWidgetComponent,
        DataExplorerDesignerPanelComponent,
        DataExplorerEditDataViewDialogComponent,
        DataExplorerWidgetAppearanceSettingsComponent,
        DataExplorerWidgetDataSettingsComponent,
        CorrelationWidgetConfigComponent,
        FieldSelectionPanelComponent,
        FieldSelectionComponent,
        FilterSelectionPanelComponent,
        GroupConfigurationComponent,
        ImageWidgetComponent,
        ImageBarComponent,
        ImageBarPreviewComponent,
        ImageWidgetConfigComponent,
        IndicatorWidgetConfigComponent,
        TimeSeriesChartWidgetConfigComponent,
        LoadDataSpinnerComponent,
        NoDataInDateRangeComponent,
        SelectPropertiesComponent,
        SelectColorPropertiesComponent,
        SelectPropertyComponent,
        TableWidgetComponent,
        TableWidgetConfigComponent,
        MapWidgetConfigComponent,
        MapWidgetComponent,
        HeatmapWidgetConfigComponent,
        ImageViewerComponent,
        TimeRangeSelectorComponent,
        DataExplorerVisualisationSettingsComponent,
        GroupSelectionPanelComponent,
        DataExplorerVisualisationSettingsComponent,
        WidgetDirective,
        TooMuchDataComponent,
        SpEchartsWidgetComponent,
        SpValueHeatmapWidgetConfigComponent,
        SpHistogramChartWidgetConfigComponent,
        SpPieChartWidgetConfigComponent,
        SpInvalidConfigurationComponent,
        SpConfigurationBoxComponent,
        SpVisualizationConfigOuterComponent,
    ],
    providers: [SemanticTypeUtilsService],
    exports: [],
})
export class DataExplorerModule {
    constructor() {}
}
