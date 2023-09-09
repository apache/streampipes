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

import {
    DataExplorerField,
    DataExplorerWidgetModel,
} from '@streampipes/platform-services';
import { EChartsOption, SeriesOption } from 'echarts';
import { SpBaseEchartsRenderer } from './sp-base-echarts-renderer';
import { GeneratedDataset, WidgetSize } from '../models/dataset.model';
import { DataTransformOption } from 'echarts/types/src/data/helper/transform';
import { WidgetBaseAppearanceConfig } from '../models/dataview-dashboard.model';

export abstract class SpBaseSingleFieldEchartsRenderer<
    T extends DataExplorerWidgetModel,
    S extends SeriesOption,
> extends SpBaseEchartsRenderer<T> {
    applyOptions(
        datasets: GeneratedDataset,
        options: EChartsOption,
        widgetConfig: T,
        widgetSize: WidgetSize,
    ) {
        const affectedField = this.getAffectedField(widgetConfig);
        const sourceIndex = affectedField.sourceIndex;
        const tags = datasets.tagValues[sourceIndex];
        const rawDatasetStartIndex = datasets.rawDataStartIndices[sourceIndex];
        const numberOfCharts = tags.length === 0 ? 1 : tags.length;
        const series = [];
        const seriesStartIndex = this.computeSeriesStart(datasets);

        for (let i = 0; i < numberOfCharts; i++) {
            datasets.dataset.push({
                fromDatasetIndex: rawDatasetStartIndex + i,
                transform: this.addDatasetTransform(widgetConfig),
            });
            const seriesName =
                tags.length === 0
                    ? this.getDefaultSeriesName(widgetConfig)
                    : this.makeSeriesName(tags[i]);
            series.push(
                this.addSeriesItem(
                    seriesName,
                    seriesStartIndex + i,
                    widgetConfig,
                    i,
                ),
            );
        }
        const gridOptions = this.makeGrid(tags.length, widgetSize);
        options.grid = gridOptions.grid;
        if (this.showAxes()) {
            const a = this.makeAxisOptions(
                widgetConfig.baseAppearanceConfig as WidgetBaseAppearanceConfig,
                this.getXAxisType(),
                this.getYAxisType(),
                gridOptions.numberOfRows * gridOptions.numberOfColumns,
            );
            options.xAxis = a.xAxisOptions;
            options.yAxis = a.yAxisOptions;
        }
        if (this.shouldApplySeriesPosition()) {
            this.applySeriesPosition(series, gridOptions.grid);
        }
        if (numberOfCharts > 1) {
            this.addSeriesTitles(options, series, gridOptions.grid);
        }
        options.dataset = datasets.dataset;
        options.series = series;
        this.addAdditionalConfigs(options);
    }

    showAxes(): boolean {
        return true;
    }

    abstract addDatasetTransform(widgetConfig: T): DataTransformOption;

    abstract addAdditionalConfigs(option: EChartsOption): void;

    abstract addSeriesItem(
        name: string,
        datasetIndex: number,
        widgetConfig: T,
        index: number,
    ): S;

    abstract getAffectedField(widgetConfig: T): DataExplorerField;

    private computeSeriesStart(datasets: GeneratedDataset) {
        return (
            datasets.rawDataEndIndices[datasets.rawDataEndIndices.length - 1] +
            1
        );
    }

    shouldApplySeriesPosition(): boolean {
        return false;
    }

    getXAxisType(): 'category' | 'value' | 'time' | 'log' {
        return 'category';
    }

    getYAxisType(): 'category' | 'value' | 'time' | 'log' {
        return 'category';
    }

    getDefaultSeriesName(widgetConfig: T): string {
        return 'Default';
    }
}
