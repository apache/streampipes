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
import { TagValue } from '../models/dataset.model';
import { EChartsOption } from 'echarts';
import { GridOption } from 'echarts/types/dist/shared';

@Injectable({ providedIn: 'root' })
export class EchartsUtilsService {
    makeSeriesName(tag: TagValue): string {
        return tag.tagKeys.toString() + ' ' + tag.values.toString();
    }

    toTagString(tagValue: TagValue, fieldName: string): string {
        const result: string[] = [];
        for (let i = 0; i < tagValue.tagKeys.length; i++) {
            result.push(fieldName + ' (' + tagValue.values[i] + ')');
        }
        return result.join(', ');
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
}
