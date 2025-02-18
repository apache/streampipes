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

import { IWidget } from '../models/dataview-dashboard.model';
import { Injectable } from '@angular/core';
import { TableWidgetConfigComponent } from '../components/charts/table/config/table-widget-config.component';
import { TableWidgetComponent } from '../components/charts/table/table-widget.component';
import { MapWidgetConfigComponent } from '../components/charts/map/config/map-widget-config.component';
import { MapWidgetComponent } from '../components/charts/map/map-widget.component';
import { HeatmapWidgetConfigComponent } from '../components/charts/heatmap/config/heatmap-widget-config.component';
import { TimeSeriesChartWidgetConfigComponent } from '../components/charts/time-series-chart/config/time-series-chart-widget-config.component';
import { ImageWidgetConfigComponent } from '../components/charts/image/config/image-widget-config.component';
import { ImageWidgetComponent } from '../components/charts/image/image-widget.component';
import { IndicatorWidgetConfigComponent } from '../components/charts/indicator/config/indicator-chart-widget-config.component';
import { CorrelationWidgetConfigComponent } from '../components/charts/correlation-chart/config/correlation-chart-widget-config.component';
import { SpEchartsWidgetComponent } from '../components/charts/base/echarts-widget.component';
import { HeatmapWidgetModel } from '../components/charts/heatmap/model/heatmap-widget.model';
import { SpValueHeatmapWidgetConfigComponent } from '../components/charts/value-heatmap/config/value-heatmap-chart-widget-config.component';
import { SpHistogramChartWidgetConfigComponent } from '../components/charts/histogram/config/histogram-chart-widget-config.component';
import { SpPieChartWidgetConfigComponent } from '../components/charts/pie/config/pie-chart-widget-config.component';
import { HistogramChartWidgetModel } from '../components/charts/histogram/model/histogram-chart-widget.model';
import { PieChartWidgetModel } from '../components/charts/pie/model/pie-chart-widget.model';
import { ValueHeatmapChartWidgetModel } from '../components/charts/value-heatmap/model/value-heatmap-chart-widget.model';
import { SpHistogramRendererService } from '../components/charts/histogram/histogram-renderer.service';
import { SpHeatmapRendererService } from '../components/charts/heatmap/heatmap-renderer.service';
import { SpPieRendererService } from '../components/charts/pie/pie-renderer.service';
import { SpValueHeatmapRendererService } from '../components/charts/value-heatmap/value-heatmap-renderer.service';
import { CorrelationChartWidgetModel } from '../components/charts/correlation-chart/model/correlation-chart-widget.model';
import { SpScatterRendererService } from '../components/charts/scatter/scatter-renderer.service';
import { SpDensityRendererService } from '../components/charts/density/density-renderer.service';
import { IndicatorChartWidgetModel } from '../components/charts/indicator/model/indicator-chart-widget.model';
import { SpIndicatorRendererService } from '../components/charts/indicator/indicator-renderer.service';
import { TimeSeriesChartWidgetModel } from '../components/charts/time-series-chart/model/time-series-chart-widget.model';
import { SpTimeseriesRendererService } from '../components/charts/time-series-chart/sp-timeseries-renderer.service';
import { SpEchartsWidgetAppearanceConfigComponent } from '../components/chart-config/echarts-widget-appearance-config/echarts-widget-appearance-config.component';
import { SpTimeSeriesAppearanceConfigComponent } from '../components/charts/time-series-chart/appearance-config/time-series-appearance-config.component';
import { SpGaugeRendererService } from '../components/charts/gauge/gauge-renderer.service';
import { GaugeWidgetConfigComponent } from '../components/charts/gauge/config/gauge-widget-config.component';
import { GaugeWidgetModel } from '../components/charts/gauge/model/gauge-widget.model';
import { TrafficLightWidgetConfigComponent } from '../components/charts/traffic-light/config/traffic-light-widget-config.component';
import { TrafficLightWidgetComponent } from '../components/charts/traffic-light/traffic-light-widget.component';
import { StatusWidgetConfigComponent } from '../components/charts/status/config/status-widget-config.component';
import { StatusWidgetComponent } from '../components/charts/status/status-widget.component';
import { TranslateService } from '@ngx-translate/core';

@Injectable({ providedIn: 'root' })
export class DataExplorerChartRegistry {
    chartTypes: IWidget<any>[] = [];

    constructor(
        private gaugeRenderer: SpGaugeRendererService,
        private heatmapRenderer: SpHeatmapRendererService,
        private histogramRenderer: SpHistogramRendererService,
        private pieRenderer: SpPieRendererService,
        private valueHeatmapRenderer: SpValueHeatmapRendererService,
        private scatterRenderer: SpScatterRendererService,
        private densityRenderer: SpDensityRendererService,
        private indicatorRenderer: SpIndicatorRendererService,
        private timeseriesRenderer: SpTimeseriesRendererService,
        private translateService: TranslateService,
    ) {
        this.chartTypes = [
            {
                id: 'gauge',
                label: this.translateService.instant('Gauge'),
                widgetAppearanceConfigurationComponent:
                    SpEchartsWidgetAppearanceConfigComponent,
                widgetConfigurationComponent: GaugeWidgetConfigComponent,
                widgetComponent: SpEchartsWidgetComponent<GaugeWidgetModel>,
                chartRenderer: this.gaugeRenderer,
            },
            {
                id: 'table',
                label: this.translateService.instant('Table'),
                widgetConfigurationComponent: TableWidgetConfigComponent,
                widgetComponent: TableWidgetComponent,
            },
            {
                id: 'traffic-Light',
                label: this.translateService.instant('Traffic Light'),
                widgetConfigurationComponent: TrafficLightWidgetConfigComponent,
                widgetComponent: TrafficLightWidgetComponent,
            },
            {
                id: 'status',
                label: this.translateService.instant('Status'),
                widgetConfigurationComponent: StatusWidgetConfigComponent,
                widgetComponent: StatusWidgetComponent,
            },
            {
                id: 'map',
                label: this.translateService.instant('Map'),
                widgetConfigurationComponent: MapWidgetConfigComponent,
                widgetComponent: MapWidgetComponent,
            },
            {
                id: 'heatmap',
                label: this.translateService.instant('Time-Series Heatmap'),
                widgetAppearanceConfigurationComponent:
                    SpEchartsWidgetAppearanceConfigComponent,
                widgetConfigurationComponent: HeatmapWidgetConfigComponent,
                widgetComponent: SpEchartsWidgetComponent<HeatmapWidgetModel>,
                chartRenderer: this.heatmapRenderer,
            },
            {
                id: 'time-series-chart',
                label: this.translateService.instant('Time Series Chart'),
                widgetAppearanceConfigurationComponent:
                    SpTimeSeriesAppearanceConfigComponent,
                widgetConfigurationComponent:
                    TimeSeriesChartWidgetConfigComponent,
                widgetComponent:
                    SpEchartsWidgetComponent<TimeSeriesChartWidgetModel>,
                chartRenderer: this.timeseriesRenderer,
            },
            {
                id: 'image',
                label: this.translateService.instant('Image'),
                widgetConfigurationComponent: ImageWidgetConfigComponent,
                widgetComponent: ImageWidgetComponent,
            },
            {
                id: 'indicator-chart',
                label: this.translateService.instant('Indicator'),
                widgetAppearanceConfigurationComponent:
                    SpEchartsWidgetAppearanceConfigComponent,
                widgetConfigurationComponent: IndicatorWidgetConfigComponent,
                widgetComponent:
                    SpEchartsWidgetComponent<IndicatorChartWidgetModel>,
                chartRenderer: this.indicatorRenderer,
            },
            {
                id: 'scatter-chart',
                label: this.translateService.instant('Scatter'),
                widgetAppearanceConfigurationComponent:
                    SpEchartsWidgetAppearanceConfigComponent,
                widgetConfigurationComponent: CorrelationWidgetConfigComponent,
                widgetComponent:
                    SpEchartsWidgetComponent<CorrelationChartWidgetModel>,
                chartRenderer: this.scatterRenderer,
                alias: 'correlation-chart',
            },
            {
                id: 'histogram-chart',
                label: this.translateService.instant('Histogram'),
                widgetAppearanceConfigurationComponent:
                    SpEchartsWidgetAppearanceConfigComponent,
                widgetConfigurationComponent:
                    SpHistogramChartWidgetConfigComponent,
                widgetComponent:
                    SpEchartsWidgetComponent<HistogramChartWidgetModel>,
                chartRenderer: this.histogramRenderer,
                alias: 'distribution-chart',
            },
            {
                id: 'pie-chart',
                label: this.translateService.instant('Pie'),
                widgetAppearanceConfigurationComponent:
                    SpEchartsWidgetAppearanceConfigComponent,
                widgetConfigurationComponent: SpPieChartWidgetConfigComponent,
                widgetComponent: SpEchartsWidgetComponent<PieChartWidgetModel>,
                chartRenderer: this.pieRenderer,
            },
            {
                id: 'value-heatmap-chart',
                label: this.translateService.instant(
                    'Value Distribution Heatmap',
                ),
                widgetAppearanceConfigurationComponent:
                    SpEchartsWidgetAppearanceConfigComponent,
                widgetConfigurationComponent:
                    SpValueHeatmapWidgetConfigComponent,
                widgetComponent:
                    SpEchartsWidgetComponent<ValueHeatmapChartWidgetModel>,
                chartRenderer: this.valueHeatmapRenderer,
            },
            {
                id: 'density-chart',
                label: this.translateService.instant('2D Density Contour'),
                widgetAppearanceConfigurationComponent:
                    SpEchartsWidgetAppearanceConfigComponent,
                widgetConfigurationComponent: CorrelationWidgetConfigComponent,
                widgetComponent:
                    SpEchartsWidgetComponent<CorrelationChartWidgetModel>,
                chartRenderer: this.densityRenderer,
            },
        ];
    }

    getAvailableChartTemplates(): IWidget<any>[] {
        return this.chartTypes.sort((a, b) => a.label.localeCompare(b.label));
    }

    getChartTemplate(chartId: string) {
        const widget = this.chartTypes.find(widget => widget.id === chartId);
        return widget !== undefined
            ? widget
            : this.findBackwardsCompatibleChart(chartId);
    }

    getChartType(chartId: string) {
        // for backwards compatibility in v0.95.0, we return either the ID or the new ID based on the alias
        return this.getChartTemplate(chartId).id;
    }

    private findBackwardsCompatibleChart(chartId: string): IWidget<any> {
        return this.chartTypes.find(
            chart => chart.alias !== undefined && chart.alias === chartId,
        );
    }
}
