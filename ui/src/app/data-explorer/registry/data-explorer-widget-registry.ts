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
import { TableWidgetConfigComponent } from '../components/widgets/table/config/table-widget-config.component';
import { TableWidgetComponent } from '../components/widgets/table/table-widget.component';
import { MapWidgetConfigComponent } from '../components/widgets/map/config/map-widget-config.component';
import { MapWidgetComponent } from '../components/widgets/map/map-widget.component';
import { HeatmapWidgetConfigComponent } from '../components/widgets/heatmap/config/heatmap-widget-config.component';
import { TimeSeriesChartWidgetConfigComponent } from '../components/widgets/time-series-chart/config/time-series-chart-widget-config.component';
import { ImageWidgetConfigComponent } from '../components/widgets/image/config/image-widget-config.component';
import { ImageWidgetComponent } from '../components/widgets/image/image-widget.component';
import { IndicatorWidgetConfigComponent } from '../components/widgets/indicator/config/indicator-chart-widget-config.component';
import { CorrelationWidgetConfigComponent } from '../components/widgets/correlation-chart/config/correlation-chart-widget-config.component';
import { SpEchartsWidgetComponent } from '../components/widgets/base/echarts-widget.component';
import { HeatmapWidgetModel } from '../components/widgets/heatmap/model/heatmap-widget.model';
import { SpValueHeatmapWidgetConfigComponent } from '../components/widgets/value-heatmap/config/value-heatmap-chart-widget-config.component';
import { SpHistogramChartWidgetConfigComponent } from '../components/widgets/histogram/config/histogram-chart-widget-config.component';
import { SpPieChartWidgetConfigComponent } from '../components/widgets/pie/config/pie-chart-widget-config.component';
import { HistogramChartWidgetModel } from '../components/widgets/histogram/model/histogram-chart-widget.model';
import { PieChartWidgetModel } from '../components/widgets/pie/model/pie-chart-widget.model';
import { ValueHeatmapChartWidgetModel } from '../components/widgets/value-heatmap/model/value-heatmap-chart-widget.model';
import { SpHistogramRendererService } from '../components/widgets/histogram/histogram-renderer.service';
import { SpHeatmapRendererService } from '../components/widgets/heatmap/heatmap-renderer.service';
import { SpPieRendererService } from '../components/widgets/pie/pie-renderer.service';
import { SpValueHeatmapRendererService } from '../components/widgets/value-heatmap/value-heatmap-renderer.service';
import { CorrelationChartWidgetModel } from '../components/widgets/correlation-chart/model/correlation-chart-widget.model';
import { SpScatterRendererService } from '../components/widgets/scatter/scatter-renderer.service';
import { SpDensityRendererService } from '../components/widgets/density/density-renderer.service';
import { IndicatorChartWidgetModel } from '../components/widgets/indicator/model/indicator-chart-widget.model';
import { SpIndicatorRendererService } from '../components/widgets/indicator/indicator-renderer.service';
import { TimeSeriesChartWidgetModel } from '../components/widgets/time-series-chart/model/time-series-chart-widget.model';
import { SpTimeseriesRendererService } from '../components/widgets/time-series-chart/sp-timeseries-renderer.service';
import { SpEchartsWidgetAppearanceConfigComponent } from '../components/widgets/utils/echarts-widget-appearance-config/echarts-widget-appearance-config.component';
import { SpTimeSeriesAppearanceConfigComponent } from '../components/widgets/time-series-chart/appearance-config/time-series-appearance-config.component';
import { SpGaugeRendererService } from '../components/widgets/gauge/gauge-renderer.service';
import { GaugeWidgetConfigComponent } from '../components/widgets/gauge/config/gauge-widget-config.component';
import { GaugeWidgetModel } from '../components/widgets/gauge/model/gauge-widget.model';
import { TrafficLightWidgetConfigComponent } from '../components/widgets/traffic-light/config/traffic-light-widget-config.component';
import { TrafficLightWidgetComponent } from '../components/widgets/traffic-light/traffic-light-widget.component';
import { StatusWidgetConfigComponent } from '../components/widgets/status/config/status-widget-config.component';
import { StatusWidgetComponent } from '../components/widgets/status/status-widget.component';

@Injectable({ providedIn: 'root' })
export class DataExplorerWidgetRegistry {
    widgetTypes: IWidget<any>[] = [];

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
    ) {
        this.widgetTypes = [
            {
                id: 'gauge',
                label: 'Gauge',
                widgetAppearanceConfigurationComponent:
                    SpEchartsWidgetAppearanceConfigComponent,
                widgetConfigurationComponent: GaugeWidgetConfigComponent,
                widgetComponent: SpEchartsWidgetComponent<GaugeWidgetModel>,
                chartRenderer: this.gaugeRenderer,
            },
            {
                id: 'table',
                label: 'Table',
                widgetConfigurationComponent: TableWidgetConfigComponent,
                widgetComponent: TableWidgetComponent,
            },
            {
                id: 'traffic-Light',
                label: 'Traffic Light',
                widgetConfigurationComponent: TrafficLightWidgetConfigComponent,
                widgetComponent: TrafficLightWidgetComponent,
            },
            {
                id: 'status',
                label: 'Status',
                widgetConfigurationComponent: StatusWidgetConfigComponent,
                widgetComponent: StatusWidgetComponent,
            },
            {
                id: 'map',
                label: 'Map',
                widgetConfigurationComponent: MapWidgetConfigComponent,
                widgetComponent: MapWidgetComponent,
            },
            {
                id: 'heatmap',
                label: 'Time-Series Heatmap',
                widgetAppearanceConfigurationComponent:
                    SpEchartsWidgetAppearanceConfigComponent,
                widgetConfigurationComponent: HeatmapWidgetConfigComponent,
                widgetComponent: SpEchartsWidgetComponent<HeatmapWidgetModel>,
                chartRenderer: this.heatmapRenderer,
            },
            {
                id: 'time-series-chart',
                label: 'Time Series Chart',
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
                label: 'Image',
                widgetConfigurationComponent: ImageWidgetConfigComponent,
                widgetComponent: ImageWidgetComponent,
            },
            {
                id: 'indicator-chart',
                label: 'Indicator',
                widgetAppearanceConfigurationComponent:
                    SpEchartsWidgetAppearanceConfigComponent,
                widgetConfigurationComponent: IndicatorWidgetConfigComponent,
                widgetComponent:
                    SpEchartsWidgetComponent<IndicatorChartWidgetModel>,
                chartRenderer: this.indicatorRenderer,
            },
            {
                id: 'scatter-chart',
                label: 'Scatter',
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
                label: 'Histogram',
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
                label: 'Pie',
                widgetAppearanceConfigurationComponent:
                    SpEchartsWidgetAppearanceConfigComponent,
                widgetConfigurationComponent: SpPieChartWidgetConfigComponent,
                widgetComponent: SpEchartsWidgetComponent<PieChartWidgetModel>,
                chartRenderer: this.pieRenderer,
            },
            {
                id: 'value-heatmap-chart',
                label: 'Value Distribution Heatmap',
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
                label: '2D Density Contour',
                widgetAppearanceConfigurationComponent:
                    SpEchartsWidgetAppearanceConfigComponent,
                widgetConfigurationComponent: CorrelationWidgetConfigComponent,
                widgetComponent:
                    SpEchartsWidgetComponent<CorrelationChartWidgetModel>,
                chartRenderer: this.densityRenderer,
            },
        ];
    }

    getAvailableWidgetTemplates(): IWidget<any>[] {
        return this.widgetTypes.sort((a, b) => a.label.localeCompare(b.label));
    }

    getWidgetTemplate(widgetId: string) {
        const widget = this.widgetTypes.find(widget => widget.id === widgetId);
        return widget !== undefined
            ? widget
            : this.findBackwardsCompatibleWidget(widgetId);
    }

    getDefaultWidget(): IWidget<any> {
        return this.widgetTypes.find(widget => widget.id === 'table');
    }

    getWidgetType(widgetType: string) {
        // for backwards compatibility in v0.95.0, we return either the ID or the new ID based on the alias
        return this.getWidgetTemplate(widgetType).id;
    }

    private findBackwardsCompatibleWidget(widgetId: string): IWidget<any> {
        return this.widgetTypes.find(
            widget => widget.alias !== undefined && widget.alias === widgetId,
        );
    }
}
