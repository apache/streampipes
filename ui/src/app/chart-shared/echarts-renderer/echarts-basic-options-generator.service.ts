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
import { EChartsOption, ToolboxComponentOption } from 'echarts';
import { SpEchartsToolboxService } from '@streampipes/shared-ui';
import type { ToolboxFeatureOption } from 'echarts/types/src/component/toolbox/featureManager.d.ts';
import { WidgetEchartsAppearanceConfig } from '../models/dataview-dashboard.model';
import { ChartThemeService } from '../services/chart-theme.service';

/**
 * Toolbox geometry shared by all charts, so that renderers can reserve the
 * exact space the toolbox occupies above the plot.
 */
export const TOOLBOX_TOP = 4;
export const TOOLBOX_ITEM_SIZE = 15;
export const TOOLBOX_PADDING = 4;
export const TOOLBOX_HEIGHT = TOOLBOX_ITEM_SIZE + 2 * TOOLBOX_PADDING;
/** Distance of the overlay toolbox from the right edge of the chart. */
export const TOOLBOX_OVERLAY_RIGHT = 8;
/** Legend geometry of the base configuration. */
export const LEGEND_TOP = 30;
export const LEGEND_ROW_HEIGHT = 24;

@Injectable({ providedIn: 'root' })
export class EchartsBasicOptionsGeneratorService {
    private echartsToolboxService = inject(SpEchartsToolboxService);
    private themeService = inject(ChartThemeService);

    makeBaseConfig(
        appearanceConfig: WidgetEchartsAppearanceConfig,
        additionalToolboxItems: Record<string, ToolboxFeatureOption> = {},
        toolboxOverlay = false,
    ): EChartsOption {
        appearanceConfig.chartAppearance ??= {
            showToolbox: true,
            showLegend: true,
            showTooltip: true,
        };

        return {
            legend: {
                type: 'scroll',
                orient: 'horizontal',
                top: LEGEND_TOP,
                show: appearanceConfig.chartAppearance.showLegend,
            },
            tooltip: {
                show: appearanceConfig.chartAppearance.showTooltip,
            },
            toolbox: {
                left: 10,
                top: TOOLBOX_TOP,
                padding: TOOLBOX_PADDING,
                itemSize: TOOLBOX_ITEM_SIZE,
                show: appearanceConfig.chartAppearance.showToolbox,
                feature: this.withTitleStyle({
                    ...this.echartsToolboxService.getAllToolboxItems(),
                    ...additionalToolboxItems,
                }),
                ...(toolboxOverlay
                    ? this.overlayToolboxPlacement(
                          LEGEND_TOP + LEGEND_ROW_HEIGHT + 4,
                      )
                    : {}),
            },
        };
    }

    /**
     * Placement of the toolbox when it is an overlay shown on hover: a
     * floating bar in the top right corner, `top` pixels from the top, on
     * a surface background so that it stays readable over the plot.
     */
    overlayToolboxPlacement(top: number): Partial<ToolboxComponentOption> {
        return {
            left: 'auto',
            right: TOOLBOX_OVERLAY_RIGHT,
            top,
            backgroundColor: this.themeService.variable(
                '--color-bg-0',
                '#ffffff',
            ),
            borderRadius: 4,
        };
    }

    /**
     * The title shown while an icon is hovered is drawn as a label below the
     * icon, where it can cover the legend. Styling it like a tooltip keeps
     * it readable on top of other chart content.
     */
    private withTitleStyle(
        features: Record<string, ToolboxFeatureOption>,
    ): Record<string, ToolboxFeatureOption> {
        const titleStyle = this.makeTitleStyle();
        return Object.fromEntries(
            Object.entries(features).map(([name, feature]) => [
                name,
                {
                    ...feature,
                    emphasis: {
                        ...feature.emphasis,
                        iconStyle: {
                            ...titleStyle,
                            ...feature.emphasis?.iconStyle,
                        },
                    },
                },
            ]),
        );
    }

    private makeTitleStyle(): NonNullable<
        NonNullable<ToolboxFeatureOption['emphasis']>['iconStyle']
    > {
        return {
            textFill: this.themeService.variable(
                '--color-default-text',
                '#121212',
            ),
            textBackgroundColor: this.themeService.variable(
                '--color-bg-2',
                '#ececec',
            ),
            textPadding: [4, 8],
            textBorderRadius: 4,
            textFontSize: 12,
        };
    }
}
