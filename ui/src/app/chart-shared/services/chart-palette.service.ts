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
import { ChartThemeService } from './chart-theme.service';

/**
 * Fallback values, identical to the light-mode defaults in
 * ui/deployment/theme/_custom-variables.scss. The theme variables win
 * whenever a document is available, so operator themes and dark mode
 * apply to chart series as well.
 */
export const DEFAULT_CHART_PALETTE: readonly string[] = [
    '#4f81bd',
    '#f28e2b',
    '#7b61ff',
    '#2a9d8f',
    '#e9c46a',
    '#c77dff',
    '#577590',
    '#f4a261',
    '#43aa8b',
    '#bc5090',
];

/**
 * The ordered categorical palette for chart series. Canvas-rendered charts
 * cannot consume CSS variables directly, so the palette is resolved from the
 * theme variables `--color-chart-1` … `--color-chart-N` at render time.
 */
@Injectable({ providedIn: 'root' })
export class ChartPaletteService {
    private themeService = inject(ChartThemeService);

    get size(): number {
        return DEFAULT_CHART_PALETTE.length;
    }

    /** The colour for the n-th series or category (wraps around). */
    color(index: number): string {
        const normalizedIndex =
            ((Math.round(index) % this.size) + this.size) % this.size;
        return this.themeService.variable(
            `--color-chart-${normalizedIndex + 1}`,
            DEFAULT_CHART_PALETTE[normalizedIndex],
        );
    }

    /** The whole palette in assignment order. */
    colors(): string[] {
        return DEFAULT_CHART_PALETTE.map((_, index) => this.color(index));
    }
}
