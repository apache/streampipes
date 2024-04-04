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
import { CommonModule } from '@angular/common';
import { FlexLayoutModule } from '@ngbracket/ngx-layout';
import { GridsterModule } from 'angular-gridster2';
import { DashboardPanelComponent } from './components/panel/dashboard-panel.component';
import { MatTabsModule } from '@angular/material/tabs';
import { DashboardWidgetComponent } from './components/widget/dashboard-widget.component';
import { FormsModule } from '@angular/forms';
import { ColorPickerModule } from 'ngx-color-picker';
import { AddVisualizationDialogComponent } from './dialogs/add-widget/add-visualization-dialog.component';
import { MatGridListModule } from '@angular/material/grid-list';
import { NumberWidgetComponent } from './components/widgets/number/number-widget.component';
import { DashboardOverviewComponent } from './components/overview/dashboard-overview.component';
import { EditDashboardDialogComponent } from './dialogs/edit-dashboard/edit-dashboard-dialog.component';
import { DashboardGridComponent } from './components/grid/dashboard-grid.component';
import { LineWidgetComponent } from './components/widgets/line/line-widget.component';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { TableWidgetComponent } from './components/widgets/table/table-widget.component';
import { CdkTableModule } from '@angular/cdk/table';
import { GaugeWidgetComponent } from './components/widgets/gauge/gauge-widget.component';
import { ImageWidgetComponent } from './components/widgets/image/image-widget.component';
import { AreaWidgetComponent } from './components/widgets/area/area-widget.component';
import { MapWidgetComponent } from './components/widgets/map/map-widget.component';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';
import { RawWidgetComponent } from './components/widgets/raw/raw-widget.component';
import { HtmlWidgetComponent } from './components/widgets/html/html-widget.component';
import { TrafficLightWidgetComponent } from './components/widgets/trafficlight/traffic-light-widget.component';
import { StandaloneDashboardComponent } from './components/standalone/standalone-dashboard.component';
import { CoreUiModule } from '../core-ui/core-ui.module';
import { WordcloudWidgetComponent } from './components/widgets/wordcloud/wordcloud-widget.component';
import { NgxEchartsModule } from 'ngx-echarts';
import { StatusWidgetComponent } from './components/widgets/status/status-widget.component';
import { BarRaceWidgetComponent } from './components/widgets/bar-race/bar-race-widget.component';
import { StackedLineChartWidgetComponent } from './components/widgets/stacked-line-chart/stacked-line-chart-widget.component';
import { PlatformServicesModule } from '@streampipes/platform-services';
import { ServicesModule } from '../services/services.module';
import { RouterModule } from '@angular/router';
import { SharedUiModule } from '@streampipes/shared-ui';
import { DashboardPanelCanDeactivateGuard } from './dashboard.can-deactivate.guard';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatTableModule } from '@angular/material/table';
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
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatStepperModule } from '@angular/material/stepper';
import { MatRadioModule } from '@angular/material/radio';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatChipsModule } from '@angular/material/chips';
import { MatSliderModule } from '@angular/material/slider';

@NgModule({
    imports: [
        MatCardModule,
        MatCheckboxModule,
        MatDialogModule,
        MatIconModule,
        MatInputModule,
        MatListModule,
        MatMenuModule,
        MatSelectModule,
        MatSidenavModule,
        MatSlideToggleModule,
        MatToolbarModule,
        MatStepperModule,
        MatRadioModule,
        MatAutocompleteModule,
        MatExpansionModule,
        MatPaginatorModule,
        MatSortModule,
        MatTooltipModule,
        MatProgressBarModule,
        MatButtonToggleModule,
        MatChipsModule,
        MatSliderModule,
        NgxEchartsModule.forRoot({
            /**
             * This will import all modules from echarts.
             * If you only need custom modules,
             * please refer to [Custom Build] section.
             */
            echarts: () => import('echarts'),
        }),
        CommonModule,
        CoreUiModule,
        MatTabsModule,
        GridsterModule,
        FlexLayoutModule,
        FormsModule,
        ColorPickerModule,
        MatGridListModule,
        MatDividerModule,
        MatFormFieldModule,
        MatTableModule,
        MatButtonModule,
        NgxChartsModule,
        CdkTableModule,
        LeafletModule,
        PlatformServicesModule,
        ServicesModule,
        SharedUiModule,
        RouterModule.forChild([
            {
                path: '',
                children: [
                    {
                        path: '',
                        component: DashboardOverviewComponent,
                    },
                    {
                        path: ':id',
                        component: DashboardPanelComponent,
                        canDeactivate: [DashboardPanelCanDeactivateGuard],
                    },
                ],
            },
        ]),
    ],
    declarations: [
        BarRaceWidgetComponent,
        DashboardGridComponent,
        DashboardOverviewComponent,
        DashboardPanelComponent,
        DashboardWidgetComponent,
        AddVisualizationDialogComponent,
        EditDashboardDialogComponent,
        AreaWidgetComponent,
        LineWidgetComponent,
        NumberWidgetComponent,
        TableWidgetComponent,
        GaugeWidgetComponent,
        ImageWidgetComponent,
        MapWidgetComponent,
        RawWidgetComponent,
        StackedLineChartWidgetComponent,
        HtmlWidgetComponent,
        StatusWidgetComponent,
        TrafficLightWidgetComponent,
        WordcloudWidgetComponent,
        StandaloneDashboardComponent,
    ],
    providers: [],
    exports: [DashboardWidgetComponent, StandaloneDashboardComponent],
})
export class DashboardModule {
    constructor() {}
}
