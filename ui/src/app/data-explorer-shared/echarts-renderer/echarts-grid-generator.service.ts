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

import { Injectable } from '@angular/core';
import { GridOptions, WidgetSize } from '../models/dataset.model';
import { GridOption } from 'echarts/types/dist/shared';
import { BoxLayoutOptionMixin } from 'echarts/types/src/util/types';

@Injectable({ providedIn: 'root' })
export class EchartsGridGeneratorService {
    minimumChartWidth = 200;
    minimumChartHeight = 50;
    gridMargin = 60;

    makeGrid(numberOfCharts: number, widgetSize: WidgetSize): GridOptions {
        if (numberOfCharts === 1) {
            return this.makeSingleGrid(widgetSize);
        } else {
            return this.makeGridPerGroup(numberOfCharts, widgetSize);
        }
    }

    private makeSingleGrid(widgetSize: WidgetSize): GridOptions {
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
    }

    private makeGridPerGroup(
        numberOfCharts: number,
        widgetSize: WidgetSize,
    ): GridOptions {
        const grid: GridOption[] = [];
        const totalCanvasWidth = widgetSize.width - this.gridMargin * 2;
        const totalCanvasHeight = widgetSize.height - this.gridMargin * 2 - 40;
        const numberOfColumns = Math.max(
            Math.floor(totalCanvasWidth / this.minimumChartWidth),
            1,
        );
        const numberOfRows = Math.ceil(numberOfCharts / numberOfColumns);

        const groupItemWidth =
            (widgetSize.width -
                (numberOfColumns * this.gridMargin + this.gridMargin)) /
            numberOfColumns;
        const groupItemHeight = Math.max(
            this.minimumChartHeight,
            (totalCanvasHeight - (numberOfRows - 1) * this.gridMargin) /
                numberOfRows,
        );

        for (let rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            for (
                let columnIndex = 0;
                columnIndex < numberOfColumns;
                columnIndex++
            ) {
                if (rowIndex * columnIndex < numberOfCharts) {
                    grid.push(
                        this.makeGridItem(
                            rowIndex,
                            columnIndex,
                            groupItemWidth,
                            groupItemHeight,
                        ),
                    );
                }
            }
        }
        return {
            grid: grid,
            numberOfRows,
            numberOfColumns,
        };
    }

    private makeGridItem(
        rowIndex: number,
        columnIndex: number,
        groupItemWidth: number,
        groupItemHeight: number,
    ): GridOption {
        return {
            left: Math.floor(
                columnIndex * groupItemWidth +
                    (columnIndex + 1) * this.gridMargin,
            ),
            top:
                this.gridMargin +
                rowIndex * (groupItemHeight + this.gridMargin) +
                40,
            width: groupItemWidth,
            height: groupItemHeight,
            borderWidth: 1,
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
}
