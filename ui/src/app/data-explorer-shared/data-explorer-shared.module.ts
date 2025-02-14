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

import { NgModule } from '@angular/core';
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
import { CommonModule } from '@angular/common';
import { LeafletModule } from '@bluehalo/ngx-leaflet';
import { CoreUiModule } from '../core-ui/core-ui.module';
import { MatTabsModule } from '@angular/material/tabs';
import { GridsterModule } from 'angular-gridster2';
import { FlexLayoutModule } from '@ngbracket/ngx-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ColorPickerModule } from 'ngx-color-picker';
import { MatGridListModule } from '@angular/material/grid-list';
import { CdkTableModule } from '@angular/cdk/table';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatNativeDateModule } from '@angular/material/core';
import { MatSliderModule } from '@angular/material/slider';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatChipsModule } from '@angular/material/chips';
import { PlatformServicesModule } from '@streampipes/platform-services';
import { SharedUiModule } from '@streampipes/shared-ui';
import { NgxEchartsModule } from 'ngx-echarts';
import { DataExplorerChartContainerComponent } from './components/chart-container/data-explorer-chart-container.component';
import { CorrelationWidgetConfigComponent } from './components/charts/correlation-chart/config/correlation-chart-widget-config.component';
import { GaugeWidgetConfigComponent } from './components/charts/gauge/config/gauge-widget-config.component';
import { ImageWidgetComponent } from './components/charts/image/image-widget.component';
import { ImageBarComponent } from './components/charts/image/image-bar/image-bar.component';
import { ImageBarPreviewComponent } from './components/charts/image/image-bar/image-bar-preview/image-bar-preview.component';
import { ImageWidgetConfigComponent } from './components/charts/image/config/image-widget-config.component';
import { IndicatorWidgetConfigComponent } from './components/charts/indicator/config/indicator-chart-widget-config.component';
import { TimeSeriesChartWidgetConfigComponent } from './components/charts/time-series-chart/config/time-series-chart-widget-config.component';
import { NoDataInDateRangeComponent } from './components/charts/base/no-data/no-data-in-date-range.component';
import { SelectMultiplePropertiesConfigComponent } from './components/chart-config/select-multiple-properties-config/select-multiple-properties-config.component';
import { SelectColorPropertiesConfigComponent } from './components/chart-config/select-color-properties-config/select-color-properties-config.component';
import { SelectSinglePropertyConfigComponent } from './components/chart-config/select-single-property-config/select-single-property-config.component';
import { TableWidgetComponent } from './components/charts/table/table-widget.component';
import { TableWidgetConfigComponent } from './components/charts/table/config/table-widget-config.component';
import { TrafficLightWidgetComponent } from './components/charts/traffic-light/traffic-light-widget.component';
import { TrafficLightWidgetConfigComponent } from './components/charts/traffic-light/config/traffic-light-widget-config.component';
import { StatusWidgetComponent } from './components/charts/status/status-widget.component';
import { StatusWidgetConfigComponent } from './components/charts/status/config/status-widget-config.component';
import { MapWidgetConfigComponent } from './components/charts/map/config/map-widget-config.component';
import { MapWidgetComponent } from './components/charts/map/map-widget.component';
import { HeatmapWidgetConfigComponent } from './components/charts/heatmap/config/heatmap-widget-config.component';
import { ImageViewerComponent } from './components/charts/image/image-viewer/image-viewer.component';
import { ChartDirective } from './components/chart-container/chart.directive';
import { TooMuchDataComponent } from './components/charts/base/too-much-data/too-much-data.component';
import { SpEchartsWidgetComponent } from './components/charts/base/echarts-widget.component';
import { SpValueHeatmapWidgetConfigComponent } from './components/charts/value-heatmap/config/value-heatmap-chart-widget-config.component';
import { SpHistogramChartWidgetConfigComponent } from './components/charts/histogram/config/histogram-chart-widget-config.component';
import { SpPieChartWidgetConfigComponent } from './components/charts/pie/config/pie-chart-widget-config.component';
import { SpImageContainerComponent } from './components/charts/image/image-container/image-container.component';
import { SpInvalidConfigurationComponent } from './components/charts/base/invalid-configuration/invalid-configuration.component';
import { SpVisualizationConfigOuterComponent } from './components/chart-config/visualization-config-outer/visualization-config-outer.component';
import { SpSelectAxisOptionsConfigComponent } from './components/chart-config/select-axis-options-config/select-axis-options-config.component';
import { SpTimeseriesItemConfigComponent } from './components/chart-config/select-color-properties-config/time-series-item-config/time-series-item-config.component';
import { SpEchartsWidgetAppearanceConfigComponent } from './components/chart-config/echarts-widget-appearance-config/echarts-widget-appearance-config.component';
import { SpTimeSeriesAppearanceConfigComponent } from './components/charts/time-series-chart/appearance-config/time-series-appearance-config.component';
import { SpDataZoomConfigComponent } from './components/chart-config/data-zoom-config/data-zoom-config.component';
import { TranslateModule } from '@ngx-translate/core';

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
        CdkTableModule,
        MatSnackBarModule,
        MatProgressSpinnerModule,
        ReactiveFormsModule,
        CoreUiModule,
        MatNativeDateModule,
        MatSliderModule,
        MatSlideToggleModule,
        MatChipsModule,
        PlatformServicesModule,
        SharedUiModule,
        TranslateModule.forChild(),
        NgxEchartsModule.forChild(),
    ],
    declarations: [
        DataExplorerChartContainerComponent,
        CorrelationWidgetConfigComponent,
        GaugeWidgetConfigComponent,
        ImageWidgetComponent,
        ImageBarComponent,
        ImageBarPreviewComponent,
        ImageWidgetConfigComponent,
        IndicatorWidgetConfigComponent,
        TimeSeriesChartWidgetConfigComponent,
        NoDataInDateRangeComponent,
        SelectMultiplePropertiesConfigComponent,
        SelectColorPropertiesConfigComponent,
        SelectSinglePropertyConfigComponent,
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
        ChartDirective,
        TooMuchDataComponent,
        SpEchartsWidgetComponent,
        SpValueHeatmapWidgetConfigComponent,
        SpHistogramChartWidgetConfigComponent,
        SpPieChartWidgetConfigComponent,
        SpImageContainerComponent,
        SpInvalidConfigurationComponent,
        SpVisualizationConfigOuterComponent,
        SpSelectAxisOptionsConfigComponent,
        SpTimeseriesItemConfigComponent,
        SpEchartsWidgetAppearanceConfigComponent,
        SpTimeSeriesAppearanceConfigComponent,
        SpDataZoomConfigComponent,
    ],
    exports: [DataExplorerChartContainerComponent],
})
export class DataExplorerSharedModule {
    constructor() {}
}
