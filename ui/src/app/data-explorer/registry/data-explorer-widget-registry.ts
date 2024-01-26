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
import { TimeSeriesChartWidgetComponent } from '../components/widgets/time-series-chart/time-series-chart-widget.component';
import { ImageWidgetConfigComponent } from '../components/widgets/image/config/image-widget-config.component';
import { ImageWidgetComponent } from '../components/widgets/image/image-widget.component';
import { IndicatorWidgetConfigComponent } from '../components/widgets/indicator/config/indicator-chart-widget-config.component';
import { IndicatorChartWidgetComponent } from '../components/widgets/indicator/indicator-chart-widget.component';
import { CorrelationWidgetConfigComponent } from '../components/widgets/correlation-chart/config/correlation-chart-widget-config.component';
import { CorrelationChartWidgetComponent } from '../components/widgets/correlation-chart/correlation-chart-widget.component';
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

@Injectable({ providedIn: 'root' })
export class DataExplorerWidgetRegistry {
    widgetTypes: IWidget<any>[] = [
        {
            id: 'table',
            label: 'Table',
            widgetConfigurationComponent: TableWidgetConfigComponent,
            widgetComponent: TableWidgetComponent,
        },
        {
            id: 'map',
            label: 'Map',
            widgetConfigurationComponent: MapWidgetConfigComponent,
            widgetComponent: MapWidgetComponent,
        },
        {
            id: 'heatmap',
            label: 'Heatmap',
            widgetConfigurationComponent: HeatmapWidgetConfigComponent,
            widgetComponent: SpEchartsWidgetComponent<HeatmapWidgetModel>,
            chartRenderer: this.heatmapRenderer,
        },
        {
            id: 'time-series-chart',
            label: 'Time Series',
            widgetConfigurationComponent: TimeSeriesChartWidgetConfigComponent,
            widgetComponent: TimeSeriesChartWidgetComponent,
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
            widgetConfigurationComponent: IndicatorWidgetConfigComponent,
            widgetComponent: IndicatorChartWidgetComponent,
        },
        {
            id: 'scatter-chart',
            label: 'Scatter',
            widgetConfigurationComponent: CorrelationWidgetConfigComponent,
            widgetComponent: CorrelationChartWidgetComponent,
        },
        {
            id: 'histogram-chart',
            label: 'Histogram',
            widgetConfigurationComponent: SpHistogramChartWidgetConfigComponent,
            widgetComponent:
                SpEchartsWidgetComponent<HistogramChartWidgetModel>,
            chartRenderer: this.histogramRenderer,
        },
        {
            id: 'pie-chart',
            label: 'Pie',
            widgetConfigurationComponent: SpPieChartWidgetConfigComponent,
            widgetComponent: SpEchartsWidgetComponent<PieChartWidgetModel>,
            chartRenderer: this.pieRenderer,
        },
        {
            id: 'value-heatmap-chart',
            label: 'Value Heatmap',
            widgetConfigurationComponent: SpValueHeatmapWidgetConfigComponent,
            widgetComponent:
                SpEchartsWidgetComponent<ValueHeatmapChartWidgetModel>,
            chartRenderer: this.valueHeatmapRenderer,
        },
        {
            id: 'density-chart',
            label: 'Density',
            widgetConfigurationComponent: CorrelationWidgetConfigComponent,
            widgetComponent: CorrelationChartWidgetComponent,
        },
    ];

    constructor(
        private heatmapRenderer: SpHeatmapRendererService,
        private histogramRenderer: SpHistogramRendererService,
        private pieRenderer: SpPieRendererService,
        private valueHeatmapRenderer: SpValueHeatmapRendererService,
    ) {}

    getAvailableWidgetTemplates(): IWidget<any>[] {
        return this.widgetTypes.sort((a, b) => a.label.localeCompare(b.label));
    }

    getWidgetTemplate(widgetId: string) {
        return this.widgetTypes.find(widget => widget.id === widgetId);
    }

    getDefaultWidget(): IWidget<any> {
        return this.widgetTypes.find(widget => widget.id === 'table');
    }
}
