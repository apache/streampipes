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

import { NgClass, NgStyle } from '@angular/common';
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
    detail?: string;
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
        NgClass,
        MatIcon,
    ],
})
export class IndicatorGroupCardComponent {
    @Input({ required: true }) card: IndicatorGroupCardView;
    @Input() cardWidth = 320;
    @Input() cardHeight = 240;
    @Input() grouped = false;

    get cardStyles(): Record<string, string> {
        const minDimension = Math.max(
            Math.min(this.cardWidth, this.cardHeight),
            1,
        );
        const hasSupport = !!this.card.label || !!this.card.detail;
        const hasDelta = !!this.card.deltaView;
        const compactMode = this.grouped || this.cardWidth < 320;

        const padding = this.clamp(
            minDimension * (compactMode ? 0.055 : 0.07),
            8,
            20,
        );
        const sectionGap = this.clamp(
            minDimension * (compactMode ? 0.018 : 0.028),
            4,
            12,
        );
        const copyGap = this.clamp(sectionGap * 0.7, 2, 8);
        const valueGap = this.clamp(sectionGap * 0.45, 2, 6);
        const labelSize = this.clamp(minDimension * 0.07, 11, 18);
        const detailSize = this.clamp(minDimension * 0.055, 10, 14);
        const deltaSize = this.clamp(
            minDimension * (compactMode ? 0.072 : 0.07),
            12,
            19,
        );
        const deltaMetaSize = this.clamp(
            minDimension * (compactMode ? 0.04 : 0.044),
            9,
            12,
        );
        const deltaHeight = this.clamp(
            minDimension * (compactMode ? 0.18 : 0.2),
            30,
            52,
        );
        const supportHeight =
            (this.card.label ? labelSize * 1.25 : 0) +
            (this.card.detail ? detailSize * 1.35 : 0) +
            (hasSupport ? copyGap : 0);
        const availableValueHeight =
            this.cardHeight -
            padding * 2 -
            supportHeight -
            (hasDelta ? deltaHeight + valueGap : 0);
        const valueSize = this.clamp(
            Math.min(
                this.cardWidth * (compactMode ? 0.19 : 0.22),
                availableValueHeight *
                    (compactMode
                        ? hasSupport && hasDelta
                            ? 0.72
                            : 0.78
                        : hasSupport && hasDelta
                          ? 0.76
                          : 0.82),
            ),
            compactMode ? 20 : 24,
            compactMode ? 20 : 112,
        );
        const deltaSizeAdjusted = this.clamp(
            Math.min(
                deltaSize,
                this.cardWidth * (compactMode ? 0.072 : 0.082),
                deltaHeight * (compactMode ? 0.32 : 0.36),
            ),
            10,
            18,
        );

        return {
            '--indicator-card-padding': `${padding}px`,
            '--indicator-card-gap': `${sectionGap}px`,
            '--indicator-card-copy-gap': `${copyGap}px`,
            '--indicator-card-value-gap': `${valueGap}px`,
            '--indicator-card-label-size': `${labelSize}px`,
            '--indicator-card-detail-size': `${detailSize}px`,
            '--indicator-card-value-size': `${valueSize}px`,
            '--indicator-card-delta-size': `${deltaSizeAdjusted}px`,
            '--indicator-card-delta-meta-size': `${deltaMetaSize}px`,
            '--indicator-card-delta-height': `${deltaHeight}px`,
        };
    }

    private clamp(value: number, min: number, max: number): number {
        return Math.min(Math.max(value, min), max);
    }
}
