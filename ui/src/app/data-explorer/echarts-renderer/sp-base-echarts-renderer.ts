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
    SpEchartsRenderer,
    WidgetBaseAppearanceConfig,
} from '../models/dataview-dashboard.model';
import {
    DataExplorerWidgetModel,
    SpQueryResult,
} from '@streampipes/platform-services';
import { EChartsOption } from 'echarts';
import { BoxLayoutOptionMixin } from 'echarts/types/src/util/types';
import {
    DatasetOption,
    GridOption,
    XAXisOption,
    YAXisOption,
} from 'echarts/types/dist/shared';
import {
    AxisOptions,
    GeneratedDataset,
    GridOptions,
    TagValue,
    WidgetSize,
} from '../models/dataset.model';
import { DataTransformOption } from 'echarts/types/src/data/helper/transform';

export abstract class SpBaseEchartsRenderer<T extends DataExplorerWidgetModel>
    implements SpEchartsRenderer<T>
{
    minimumChartWidth = 200;
    minimumChartHeight = 50;
    gridMargin = 60;

    abstract getType(): string;

    render(
        spQueryResult: SpQueryResult[],
        widgetConfig: T,
        widgetSize: WidgetSize,
    ): EChartsOption {
        const options = this.makeBaseConfig();
        const datasets = this.toDataset(spQueryResult, widgetConfig);
        this.applyOptions(datasets, options, widgetConfig, widgetSize);
        return options;
    }

    makeBaseConfig(): EChartsOption {
        return {
            legend: {
                orient: 'horizontal',
                top: 'top',
            },
            tooltip: {
                show: true,
            },
        };
    }

    applySeriesPosition(
        series: BoxLayoutOptionMixin[],
        gridOptions: GridOption[],
    ) {
        series.forEach((s, index) => {
            const gridOption = gridOptions[index];
            s.left = gridOption.left;
            s.top = gridOption.top;
            s.width = gridOption.width;
            s.height = gridOption.height;
        });
    }

    makeAxisOptions(
        appearanceConfig: WidgetBaseAppearanceConfig,
        xAxisType: 'category' | 'value' | 'time' | 'log',
        yAxisType: 'category' | 'value' | 'time' | 'log',
        numberOfAxes: number,
    ): AxisOptions {
        const xAxisOptions: XAXisOption[] = [];
        const yAxisOptions: YAXisOption[] = [];
        for (let i = 0; i < numberOfAxes; i++) {
            xAxisOptions.push(
                this.makeAxis(xAxisType, i, appearanceConfig) as XAXisOption,
            );
            yAxisOptions.push(
                this.makeAxis(yAxisType, i, appearanceConfig) as YAXisOption,
            );
        }

        return {
            xAxisOptions: xAxisOptions,
            yAxisOptions: yAxisOptions,
        };
    }

    makeAxis(
        axisType: 'category' | 'value' | 'time' | 'log',
        gridIndex: number,
        appearanceConfig: WidgetBaseAppearanceConfig,
    ): XAXisOption | YAXisOption {
        return {
            type: axisType,
            gridIndex: gridIndex,
            nameTextStyle: {
                color: appearanceConfig.textColor,
            },
            axisTick: {
                lineStyle: {
                    color: appearanceConfig.textColor,
                },
            },
            axisLabel: {
                color: appearanceConfig.textColor,
            },
            axisLine: {
                lineStyle: {
                    color: appearanceConfig.textColor,
                },
            },
        };
    }

    makeSeriesName(tag: TagValue): string {
        return tag.tagKeys.toString() + ' ' + tag.values.toString();
    }

    addSeriesTitles(
        options: EChartsOption,
        series: any[],
        gridOptions: GridOption[],
    ): void {
        options.title = series.map((s, index) => {
            const grid = gridOptions[index];
            return {
                text: s.name,
                textStyle: {
                    fontSize: 12,
                    fontWeight: 'bold',
                },
                left: grid.left,
                top: (grid.top as number) - 20,
            };
        });
    }

    makeGrid(tagLength: number, widgetSize: WidgetSize): GridOptions {
        if (tagLength === 0) {
            return {
                grid: [
                    {
                        height: widgetSize.height - 100,
                        width: widgetSize.width - 100,
                        top: this.gridMargin,
                        left: this.gridMargin,
                        right: this.gridMargin,
                        bottom: this.gridMargin,
                    },
                ],
                numberOfColumns: 1,
                numberOfRows: 1,
            };
        } else {
            const grid: GridOption[] = [];
            const totalCanvasWidth = widgetSize.width - this.gridMargin * 2;
            const totalCanvasHeight = widgetSize.height - this.gridMargin * 2;
            const numberOfColumns = Math.max(
                Math.floor(totalCanvasWidth / this.minimumChartWidth),
                1,
            );
            const numberOfRows = Math.ceil(tagLength / numberOfColumns);

            const groupItemWidth =
                (widgetSize.width -
                    (numberOfColumns * this.gridMargin + this.gridMargin)) /
                numberOfColumns;
            const groupItemHeight = Math.max(
                this.minimumChartHeight,
                (totalCanvasHeight - (numberOfRows - 1) * this.gridMargin) /
                    numberOfRows,
            );

            for (let i = 0; i < numberOfRows; i++) {
                for (let j = 0; j < numberOfColumns; j++) {
                    if (i * j < tagLength) {
                        grid.push({
                            left: Math.floor(
                                j * groupItemWidth + (j + 1) * this.gridMargin,
                            ),
                            top:
                                this.gridMargin +
                                i * (groupItemHeight + this.gridMargin),
                            width: groupItemWidth,
                            height: groupItemHeight,
                            borderWidth: 1,
                        });
                    }
                }
            }
            return {
                grid: grid,
                numberOfRows,
                numberOfColumns,
            };
        }
    }

    toDataset(
        queryResults: SpQueryResult[],
        widgetConfig: T,
    ): GeneratedDataset {
        const datasets: DatasetOption[] = [];
        const rawDataStartIndices: number[] = [];
        const rawDataEndIndices: number[] = [];
        const preparedDataStartIndices: number[] = [];
        const preparedDataEndIndices: number[] = [];
        const tagValues: TagValue[][] = [];
        let initialTransformsCount = 0;

        queryResults.forEach((queryResult, index) => {
            const currentDatasetSize = datasets.length;
            const initialTransforms = this.initialTransforms(
                widgetConfig,
                index,
            );
            const data = [];
            data.push(queryResult.headers);
            queryResult.allDataSeries.forEach(series => {
                data.push(...series.rows);
            });

            const tags: TagValue[] = queryResult.allDataSeries
                .map(series => series.tags)
                .filter(tags => tags !== null)
                .map(kv => {
                    return {
                        tagKeys: Object.keys(kv),
                        values: Object.values(kv),
                    };
                });

            datasets.push({
                source: data,
                dimensions: queryResult.headers,
            });

            if (initialTransforms.length > 0) {
                initialTransformsCount++;
                datasets.push({
                    fromDatasetIndex: currentDatasetSize,
                    transform: initialTransforms,
                });
            }
            tags.forEach(tag => {
                const filters = tag.tagKeys.map((key, index) => {
                    return {
                        dimension: key,
                        value: tag.values[index],
                    };
                });
                datasets.push({
                    fromDatasetIndex:
                        currentDatasetSize + initialTransforms.length,
                    transform: {
                        type: 'filter',
                        config: {
                            and: filters,
                        },
                    },
                });
            });
            tagValues.push(tags);
            rawDataStartIndices.push(
                currentDatasetSize + initialTransforms.length,
            );
            rawDataEndIndices.push(
                currentDatasetSize + initialTransforms.length + tags.length,
            );
        });

        return {
            dataset: datasets,
            tagValues: tagValues,
            rawDataStartIndices: rawDataStartIndices,
            rawDataEndIndices: rawDataEndIndices,
            preparedDataStartIndices: preparedDataStartIndices,
            preparedDataEndIndices: preparedDataEndIndices,
            initialTransformsCount: initialTransformsCount,
        };
    }

    abstract applyOptions(
        datasets: GeneratedDataset,
        options: EChartsOption,
        widgetConfig: T,
        widgetSize: WidgetSize,
    ): void;

    initialTransforms(
        widgetConfig: T,
        sourceIndex: number,
    ): DataTransformOption[] {
        return [];
    }
}
