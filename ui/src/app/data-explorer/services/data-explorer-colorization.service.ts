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
import { DataExplorerField } from '@streampipes/platform-services';

@Injectable({ providedIn: 'root' })
export class DataExplorerColorizationService {
    makeColor(
        chosenColor: Record<string, string>,
        field: DataExplorerField,
        groupIndex: number,
    ): string {
        const baseColor = chosenColor[field.fullDbName + field.sourceIndex];
        return this.adjustColorBrightness(baseColor, groupIndex);
    }

    private adjustColorBrightness(color: string, groupIndex: number): string {
        const amount = groupIndex * 15;
        let { r, g, b } = this.hexToRgb(color);

        r = this.clampColorValue(r + amount);
        g = this.clampColorValue(g + amount);
        b = this.clampColorValue(b + amount);

        return this.rgbToHex(r, g, b);
    }

    private hexToRgb(hex: string): { r: number; g: number; b: number } {
        const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
        return result
            ? {
                  r: parseInt(result[1], 16),
                  g: parseInt(result[2], 16),
                  b: parseInt(result[3], 16),
              }
            : null;
    }

    private rgbToHex(r: number, g: number, b: number): string {
        return (
            '#' +
            [r, g, b]
                .map(x => {
                    const hex = x.toString(16);
                    return hex.length === 1 ? '0' + hex : hex;
                })
                .join('')
        );
    }

    private clampColorValue(value: number): number {
        return Math.max(0, Math.min(255, value));
    }
}
