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
import {
    DefaultMatCalendarRangeStrategy,
    MatDatepickerModule,
    MatRangeDateSelectionModel,
} from '@angular/material/datepicker';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSliderModule } from '@angular/material/slider';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTabsModule } from '@angular/material/tabs';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';

import { NgxChartsModule } from '@swimlane/ngx-charts';
import { GridsterModule } from 'angular-gridster2';
import { ColorPickerModule } from 'ngx-color-picker';
import { PlatformServicesModule } from '@streampipes/platform-services';
import { CoreUiModule } from '../core-ui/core-ui.module';
import { DataExplorerDashboardGridComponent } from './components/widget-view/grid-view/data-explorer-dashboard-grid.component';
import { DataExplorerOverviewComponent } from './components/overview/data-explorer-overview.component';
import { DataExplorerDashboardPanelComponent } from './components/dashboard/data-explorer-dashboard-panel.component';
import { TimeRangeSelectorComponent } from './components/time-selector/time-range-selector.component';
import { DataExplorerDashboardWidgetComponent } from './components/widget/data-explorer-dashboard-widget.component';
import { ImageWidgetComponent } from './components/widgets/image/image-widget.component';
import { TrafficLightWidgetComponent } from './components/widgets/traffic-light/traffic-light-widget.component';
import { TrafficLightWidgetConfigComponent } from './components/widgets/traffic-light/config/traffic-light-widget-config.component';
import { StatusWidgetComponent } from './components/widgets/status/status-widget.component';
import { StatusWidgetConfigComponent } from './components/widgets/status/config/status-widget-config.component';
import { TableWidgetComponent } from './components/widgets/table/table-widget.component';
import { AggregateConfigurationComponent } from './components/widgets/utils/aggregate-configuration/aggregate-configuration.component';
import { LoadDataSpinnerComponent } from './components/widgets/utils/load-data-spinner/load-data-spinner.component';
import { NoDataInDateRangeComponent } from './components/widgets/utils/no-data/no-data-in-date-range.component';
import { SelectPropertiesComponent } from './components/widgets/utils/select-properties/select-properties.component';
import { SelectColorPropertiesComponent } from './components/widgets/utils/select-color-properties/select-color-properties.component';
import { DataExplorerEditDashboardDialogComponent } from './dialogs/edit-dashboard/data-explorer-edit-dashboard-dialog.component';
import { GroupConfigurationComponent } from './components/widgets/utils/group-configuration/group-configuration.component';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { DataExplorerDesignerPanelComponent } from './components/data-view/data-view-designer-panel/data-explorer-designer-panel.component';
import { TableWidgetConfigComponent } from './components/widgets/table/config/table-widget-config.component';
import { MapWidgetComponent } from './components/widgets/map/map-widget.component';
import { MapWidgetConfigComponent } from './components/widgets/map/config/map-widget-config.component';
import { HeatmapWidgetConfigComponent } from './components/widgets/heatmap/config/heatmap-widget-config.component';
import { DataExplorerWidgetAppearanceSettingsComponent } from './components/data-view/data-view-designer-panel/appearance-settings/data-explorer-widget-appearance-settings.component';
import { DataExplorerWidgetDataSettingsComponent } from './components/data-view/data-view-designer-panel/data-settings/data-explorer-widget-data-settings.component';
import { TimeSeriesChartWidgetConfigComponent } from './components/widgets/time-series-chart/config/time-series-chart-widget-config.component';
import { ImageWidgetConfigComponent } from './components/widgets/image/config/image-widget-config.component';
import { IndicatorWidgetConfigComponent } from './components/widgets/indicator/config/indicator-chart-widget-config.component';
import { FieldSelectionPanelComponent } from './components/data-view/data-view-designer-panel/data-settings/field-selection-panel/field-selection-panel.component';
import { FieldSelectionComponent } from './components/data-view/data-view-designer-panel/data-settings/field-selection/field-selection.component';
import { FilterSelectionPanelComponent } from './components/data-view/data-view-designer-panel/data-settings/filter-selection-panel/filter-selection-panel.component';
import { SelectPropertyComponent } from './components/widgets/utils/select-property/select-property.component';
import { DataExplorerVisualisationSettingsComponent } from './components/data-view/data-view-designer-panel/visualisation-settings/data-explorer-visualisation-settings.component';
import { GroupSelectionPanelComponent } from './components/data-view/data-view-designer-panel/data-settings/group-selection-panel/group-selection-panel.component';
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
import { SpSelectAxisOptionsComponent } from './components/widgets/utils/select-axis-options/select-axis-options.component';
import { SpTimeseriesItemConfigComponent } from './components/widgets/utils/select-color-properties/time-series-item-config/time-series-item-config.component';
import { SpEchartsWidgetAppearanceConfigComponent } from './components/widgets/utils/echarts-widget-appearance-config/echarts-widget-appearance-config.component';
import { SpTimeSeriesAppearanceConfigComponent } from './components/widgets/time-series-chart/appearance-config/time-series-appearance-config.component';
import { SpDataZoomConfigComponent } from './components/widgets/utils/data-zoom-config/data-zoom-config.component';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatSelectModule } from '@angular/material/select';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatStepperModule } from '@angular/material/stepper';
import { MatRadioModule } from '@angular/material/radio';
import { MatTableModule } from '@angular/material/table';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatDividerModule } from '@angular/material/divider';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { SpImageContainerComponent } from './components/widgets/image/image-container/image-container.component';
import { SpDataExplorerDataViewOverviewComponent } from './components/overview/data-explorer-data-view-overview/data-explorer-data-view-overview.component';
import { SpDataExplorerDashboardOverviewComponent } from './components/overview/data-explorer-dashboard-overview/data-explorer-dashboard-overview.component';
import { DataExplorerDataViewComponent } from './components/data-view/data-explorer-data-view.component';
import { DataExplorerDataViewToolbarComponent } from './components/data-view/data-view-toolbar/data-explorer-data-view-toolbar.component';
import { DataExplorerDataViewSelectionComponent } from './components/dashboard/dashboard-widget-selection-panel/data-view-selection/data-view-selection.component';
import { DataExplorerDashboardWidgetSelectionPanelComponent } from './components/dashboard/dashboard-widget-selection-panel/dashboard-widget-selection-panel.component';
import { DataExplorerDataViewPreviewComponent } from './components/dashboard/dashboard-widget-selection-panel/data-view-selection/data-view-preview/data-view-preview.component';
import { DataExplorerDashboardToolbarComponent } from './components/dashboard/dashboard-toolbar/dashboard-toolbar.component';
import { TimeRangeSelectorMenuComponent } from './components/time-selector/time-selector-menu/time-selector-menu.component';
import { CustomTimeRangeSelectionComponent } from './components/time-selector/time-selector-menu/custom-time-range-selection/custom-time-range-selection.component';
import { DataExplorerRefreshIntervalSettingsComponent } from './components/dashboard/dashboard-toolbar/refresh-interval-settings/refresh-interval-settings.component';
import { OrderSelectionPanelComponent } from './components/data-view/data-view-designer-panel/data-settings/order-selection-panel/order-selection-panel.component';
import { GaugeWidgetConfigComponent } from './components/widgets/gauge/config/gauge-widget-config.component';

@NgModule({
    imports: [
        MatButtonModule,
        MatCardModule,
        MatCheckboxModule,
        MatDialogModule,
        MatIconModule,
        MatInputModule,
        MatListModule,
        MatMenuModule,
        MatSelectModule,
        MatSidenavModule,
        MatToolbarModule,
        MatStepperModule,
        MatRadioModule,
        MatTableModule,
        MatAutocompleteModule,
        MatExpansionModule,
        MatPaginatorModule,
        MatSortModule,
        MatDividerModule,
        MatTooltipModule,
        MatProgressBarModule,
        MatButtonToggleModule,
        CommonModule,
        LeafletModule,
        CoreUiModule,
        MatTabsModule,
        GridsterModule,
        FlexLayoutModule,
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
                path: '',
                children: [
                    {
                        path: '',
                        component: DataExplorerOverviewComponent,
                    },
                    {
                        path: 'data-view',
                        component: DataExplorerDataViewComponent,
                    },
                    {
                        path: 'data-view/:id',
                        component: DataExplorerDataViewComponent,
                        canDeactivate: [DataExplorerPanelCanDeactivateGuard],
                    },
                    {
                        path: 'dashboard/:id',
                        component: DataExplorerDashboardPanelComponent,
                        canDeactivate: [DataExplorerPanelCanDeactivateGuard],
                    },
                    {
                        path: 'dashboard/:id/:startTime/:endTime',
                        component: DataExplorerDashboardPanelComponent,
                        canDeactivate: [DataExplorerPanelCanDeactivateGuard],
                    },
                ],
            },
        ]),
    ],
    declarations: [
        AggregateConfigurationComponent,
        CustomTimeRangeSelectionComponent,
        DataExplorerDashboardGridComponent,
        DataExplorerOverviewComponent,
        DataExplorerDashboardPanelComponent,
        DataExplorerDashboardSlideViewComponent,
        DataExplorerDashboardToolbarComponent,
        DataExplorerDashboardWidgetComponent,
        DataExplorerDashboardWidgetSelectionPanelComponent,
        DataExplorerDataViewPreviewComponent,
        DataExplorerDataViewSelectionComponent,
        DataExplorerDesignerPanelComponent,
        DataExplorerEditDashboardDialogComponent,
        DataExplorerRefreshIntervalSettingsComponent,
        DataExplorerWidgetAppearanceSettingsComponent,
        DataExplorerWidgetDataSettingsComponent,
        DataExplorerDataViewComponent,
        DataExplorerDataViewToolbarComponent,
        CorrelationWidgetConfigComponent,
        FieldSelectionPanelComponent,
        FieldSelectionComponent,
        FilterSelectionPanelComponent,
        GaugeWidgetConfigComponent,
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
        TrafficLightWidgetComponent,
        TrafficLightWidgetConfigComponent,
        StatusWidgetComponent,
        StatusWidgetConfigComponent,
        MapWidgetConfigComponent,
        MapWidgetComponent,
        HeatmapWidgetConfigComponent,
        ImageViewerComponent,
        TimeRangeSelectorComponent,
        TimeRangeSelectorMenuComponent,
        DataExplorerVisualisationSettingsComponent,
        GroupSelectionPanelComponent,
        DataExplorerVisualisationSettingsComponent,
        WidgetDirective,
        TooMuchDataComponent,
        OrderSelectionPanelComponent,
        SpDataExplorerDataViewOverviewComponent,
        SpDataExplorerDashboardOverviewComponent,
        SpEchartsWidgetComponent,
        SpValueHeatmapWidgetConfigComponent,
        SpHistogramChartWidgetConfigComponent,
        SpPieChartWidgetConfigComponent,
        SpImageContainerComponent,
        SpInvalidConfigurationComponent,
        SpConfigurationBoxComponent,
        SpVisualizationConfigOuterComponent,
        SpSelectAxisOptionsComponent,
        SpTimeseriesItemConfigComponent,
        SpEchartsWidgetAppearanceConfigComponent,
        SpTimeSeriesAppearanceConfigComponent,
        SpDataZoomConfigComponent,
    ],
    providers: [DefaultMatCalendarRangeStrategy, MatRangeDateSelectionModel],
    exports: [],
})
export class DataExplorerModule {
    constructor() {}
}
