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
import { SpBaseEchartsRenderer } from './base-echarts-renderer';
import {
    GeneratedDataset,
    GridOptions,
    TagValue,
    WidgetSize,
} from '../models/dataset.model';
import { DataTransformOption } from 'echarts/types/src/data/helper/transform';
import { WidgetBaseAppearanceConfig } from '../models/dataview-dashboard.model';
import { BoxLayoutOptionMixin } from 'echarts/types/src/util/types';
import { DatasetOption } from 'echarts/types/dist/shared';

export abstract class SpBaseSingleFieldEchartsRenderer<
    T extends DataExplorerWidgetModel,
    S extends SeriesOption,
> extends SpBaseEchartsRenderer<T> {
    applyOptions(
        generatedDataset: GeneratedDataset,
        options: EChartsOption,
        widgetConfig: T,
        widgetSize: WidgetSize,
    ) {
        const selectedField = this.getSelectedField(widgetConfig);
        const tags = this.datasetUtilsService.getTags(
            generatedDataset,
            selectedField.sourceIndex,
        );
        const numberOfCharts = this.getNumberOfCharts(tags);
        const seriesStartIndex = this.getSeriesStartIndex(
            generatedDataset,
            selectedField.sourceIndex,
        );
        const datasets =
            this.datasetUtilsService.toEChartsDataset(generatedDataset);

        const gridOptions = this.gridGeneratorService.makeGrid(
            numberOfCharts,
            widgetSize,
        );
        const series = this.buildSeries(
            datasets,
            numberOfCharts,
            tags,
            widgetConfig,
            seriesStartIndex,
        );
        this.configureGrid(
            options,
            series as BoxLayoutOptionMixin[],
            gridOptions,
        );
        this.configureAxes(options, widgetConfig, numberOfCharts, series);
        this.finalizeOptions(options, datasets, series, gridOptions);
    }

    private getNumberOfCharts(tags: TagValue[]): number {
        return tags.length === 0 ? 1 : tags.length;
    }

    private buildSeries(
        datasets: DatasetOption[],
        numberOfCharts: number,
        tags: TagValue[],
        widgetConfig: T,
        seriesStartIndex: number,
    ): S[] {
        const series: S[] = [];
        for (let i = 0; i < numberOfCharts; i++) {
            this.addDatasetToDatasets(
                datasets,
                seriesStartIndex + i,
                widgetConfig,
            );
            const seriesName = this.getSeriesName(tags, widgetConfig, i);
            series.push(
                this.addSeriesItem(
                    seriesName,
                    seriesStartIndex + i + numberOfCharts,
                    widgetConfig,
                    i,
                ),
            );
        }
        return series;
    }

    private addDatasetToDatasets(
        datasets: DatasetOption[],
        fromDatasetIndex: number,
        widgetConfig: T,
    ) {
        datasets.push({
            fromDatasetIndex: fromDatasetIndex,
            transform: this.addDatasetTransform(widgetConfig),
        });
    }

    private getSeriesName(
        tags: TagValue[],
        widgetConfig: T,
        index: number,
    ): string {
        const fieldName = this.getSelectedField(widgetConfig).fullDbName;
        return tags.length === 0
            ? this.getDefaultSeriesName(widgetConfig)
            : this.echartsUtilsService.toTagString(tags[index], fieldName);
    }

    private configureGrid(
        options: EChartsOption,
        series: BoxLayoutOptionMixin[],
        gridOptions: GridOptions,
    ) {
        options.grid = gridOptions.grid;
        if (this.shouldApplySeriesPosition()) {
            this.gridGeneratorService.applySeriesPosition(series, options.grid);
        }
    }

    private configureAxes(
        options: EChartsOption,
        widgetConfig: T,
        numberOfCharts: number,
        series: S[],
    ) {
        if (this.showAxes()) {
            const axisOptions = this.axisGeneratorService.makeAxisOptions(
                widgetConfig.baseAppearanceConfig as WidgetBaseAppearanceConfig,
                this.getXAxisType(),
                this.getYAxisType(),
                numberOfCharts,
            );
            options.xAxis = axisOptions.xAxisOptions;
            options.yAxis = axisOptions.yAxisOptions;
        }
    }

    private finalizeOptions(
        options: EChartsOption,
        dataset: DatasetOption[],
        series: S[],
        gridOptions: GridOptions,
    ) {
        if (series.length > 1) {
            this.echartsUtilsService.addSeriesTitles(
                options,
                series,
                gridOptions.grid,
            );
        }
        options.dataset = dataset;
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

    abstract getSelectedField(widgetConfig: T): DataExplorerField;

    private getSeriesStartIndex(
        datasets: GeneratedDataset,
        sourceIndex: number,
    ): number {
        const dataset = this.datasetUtilsService.findPreparedDataset(
            datasets,
            sourceIndex,
        );
        return dataset.meta.preparedDataStartIndex;
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
