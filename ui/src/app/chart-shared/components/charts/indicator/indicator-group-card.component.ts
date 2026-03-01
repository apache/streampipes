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

import { NgStyle } from '@angular/common';
import { Component, Input } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';

export type IndicatorDeltaTone = 'positive' | 'negative' | 'neutral';

export interface IndicatorDeltaView {
    icon: string;
    label: string;
    meta?: string;
    detail?: string;
    tone: IndicatorDeltaTone;
}

export interface IndicatorGroupCardView {
    id: string;
    label?: string;
    displayValue: string;
    deltaView?: IndicatorDeltaView;
}

@Component({
    selector: 'sp-indicator-group-card',
    templateUrl: './indicator-group-card.component.html',
    styleUrls: ['./indicator-group-card.component.scss'],
    imports: [
        LayoutDirective,
        LayoutAlignDirective,
        FlexDirective,
        NgStyle,
        MatIcon,
    ],
})
export class IndicatorGroupCardComponent {
    private static readonly REFERENCE_CARD_WIDTH = 560;
    private static readonly REFERENCE_CARD_HEIGHT = 320;
    private static readonly MIN_FONT_SCALE = 0.15;
    private static readonly MAX_FONT_SCALE = 1;

    @Input({ required: true }) card: IndicatorGroupCardView;
    @Input() cardWidth = 320;
    @Input() cardHeight = 240;
    @Input() grouped = false;
    @Input() manualValueFontSize?: number;
    @Input() manualDeltaFontSize?: number;
    @Input() scaleManualFonts = false;

    get cardStyles(): Record<string, string> {
        const tinyMode = this.cardWidth < 220 || this.cardHeight < 150;
        const compactMode =
            this.grouped || this.cardWidth < 320 || this.cardHeight < 220;

        const defaults = tinyMode
            ? {
                  padding: 8,
                  copyGap: 3,
                  valueGap: 2,
                  labelSize: 11,
                  valueSize: 34,
                  deltaSize: 13,
                  deltaMetaSize: 9,
                  deltaHeight: 24,
              }
            : compactMode
              ? {
                    padding: 10,
                    copyGap: 4,
                    valueGap: 4,
                    labelSize: 12,
                    valueSize: 48,
                    deltaSize: 16,
                    deltaMetaSize: 10,
                    deltaHeight: 30,
                }
              : {
                    padding: 16,
                    copyGap: 6,
                    valueGap: 6,
                    labelSize: 14,
                    valueSize: 72,
                    deltaSize: 20,
                    deltaMetaSize: 12,
                    deltaHeight: 38,
                };

        return {
            '--indicator-card-padding': `${defaults.padding}px`,
            '--indicator-card-copy-gap': `${defaults.copyGap}px`,
            '--indicator-card-value-gap': `${defaults.valueGap}px`,
            '--indicator-card-label-size': `${defaults.labelSize}px`,
            '--indicator-card-value-size': `${this.resolveManualFontSize(
                this.manualValueFontSize,
                defaults.valueSize,
            )}px`,
            '--indicator-card-delta-size': `${this.resolveManualFontSize(
                this.manualDeltaFontSize,
                defaults.deltaSize,
            )}px`,
            '--indicator-card-delta-meta-size': `${defaults.deltaMetaSize}px`,
            '--indicator-card-delta-height': `${defaults.deltaHeight}px`,
        };
    }

    private resolveManualFontSize(
        manualSize: number | undefined,
        fallbackSize: number,
    ): number {
        if (
            manualSize === undefined ||
            manualSize === null ||
            Number.isNaN(Number(manualSize)) ||
            manualSize <= 0
        ) {
            return fallbackSize;
        }

        if (!this.scaleManualFonts) {
            return manualSize;
        }

        const widthScale =
            this.cardWidth / IndicatorGroupCardComponent.REFERENCE_CARD_WIDTH;
        const heightScale =
            this.cardHeight / IndicatorGroupCardComponent.REFERENCE_CARD_HEIGHT;
        const scale = this.clamp(
            Math.min(widthScale, heightScale),
            IndicatorGroupCardComponent.MIN_FONT_SCALE,
            IndicatorGroupCardComponent.MAX_FONT_SCALE,
        );

        return Math.round(manualSize * scale * 10) / 10;
    }

    private clamp(value: number, min: number, max: number): number {
        return Math.min(Math.max(value, min), max);
    }
}
