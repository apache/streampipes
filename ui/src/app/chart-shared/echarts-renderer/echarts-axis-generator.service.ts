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
import { WidgetBaseAppearanceConfig } from '../models/dataview-dashboard.model';
import { AxisOptions } from '../models/dataset.model';
import { XAXisOption, YAXisOption } from 'echarts/types/dist/shared';

@Injectable({ providedIn: 'root' })
export class EchartsAxisGeneratorService {
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
}
