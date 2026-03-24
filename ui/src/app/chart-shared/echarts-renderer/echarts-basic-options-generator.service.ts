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

import { inject, Injectable } from '@angular/core';
import { EChartsOption } from 'echarts';
import { SpEchartsToolboxService } from '@streampipes/shared-ui';
import type { ToolboxFeatureOption } from 'echarts/types/src/component/toolbox/featureManager.d.ts';
import { WidgetEchartsAppearanceConfig } from '../models/dataview-dashboard.model';

@Injectable({ providedIn: 'root' })
export class EchartsBasicOptionsGeneratorService {
    private echartsToolboxService = inject(SpEchartsToolboxService);

    makeBaseConfig(
        appearanceConfig: WidgetEchartsAppearanceConfig,
        additionalToolboxItems: Record<string, ToolboxFeatureOption> = {},
    ): EChartsOption {
        appearanceConfig.chartAppearance ??= {
            showToolbox: true,
            showLegend: true,
            showTooltip: true,
        };
        appearanceConfig.numberFormat ??= {
            decimals: 2,
        };
        appearanceConfig.numberFormat.decimals = this.normalizeDecimals(
            appearanceConfig.numberFormat.decimals,
        );

        return {
            legend: {
                type: 'scroll',
                orient: 'horizontal',
                top: 30,
                show: appearanceConfig.chartAppearance.showLegend,
            },
            tooltip: {
                show: appearanceConfig.chartAppearance.showTooltip,
            },
            toolbox: {
                left: 10,
                show: appearanceConfig.chartAppearance.showToolbox,
                feature: {
                    ...this.echartsToolboxService.getAllToolboxItems(),
                    ...additionalToolboxItems,
                },
            },
        };
    }

    private normalizeDecimals(decimals: number): number {
        if (!Number.isFinite(decimals)) {
            return 2;
        }
        return Math.min(10, Math.max(0, Math.round(decimals)));
    }
}
