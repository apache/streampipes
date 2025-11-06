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

@Injectable({ providedIn: 'root' })
export class SpColorizationService {
    generateContrastColor(bgColor: string): string {
        const hex = bgColor.startsWith('#') ? bgColor.substring(1) : bgColor;
        const r = parseInt(hex.substring(0, 2), 16);
        const g = parseInt(hex.substring(2, 4), 16);
        const b = parseInt(hex.substring(4, 6), 16);

        // Convert to HSL
        const { h, s, l } = this.rgbToHsl(r, g, b);

        // Adjust lightness: if background is dark, make text lighter; else make it darker
        const textLightness = l > 0.5 ? l - 0.45 : l + 0.45;

        // Convert back to RGB
        const {
            r: tr,
            g: tg,
            b: tb,
        } = this.hslToRgb(h, s, Math.min(1, Math.max(0, textLightness)));

        // Return as hex
        return this.rgbToHex(tr, tg, tb);
    }

    generateRandomColor(): string {
        // Use HSL for easier color control
        const h = Math.floor(Math.random() * 360);
        const s = 0.55 + Math.random() * 0.25;
        const l = 0.45 + (Math.random() - 0.5) * 0.2;

        const { r, g, b } = this.hslToRgb(h / 360, s, l);
        return this.rgbToHex(r, g, b);
    }

    private rgbToHsl(r: number, g: number, b: number) {
        r /= 255;
        g /= 255;
        b /= 255;
        const max = Math.max(r, g, b);
        const min = Math.min(r, g, b);
        let h = 0,
            s = 0;
        const l = (max + min) / 2;

        if (max !== min) {
            const d = max - min;
            s = l > 0.5 ? d / (2 - max - min) : d / (max + min);
            switch (max) {
                case r:
                    h = (g - b) / d + (g < b ? 6 : 0);
                    break;
                case g:
                    h = (b - r) / d + 2;
                    break;
                case b:
                    h = (r - g) / d + 4;
                    break;
            }
            h /= 6;
        }

        return { h, s, l };
    }

    private hslToRgb(h: number, s: number, l: number) {
        let r: number, g: number, b: number;

        if (s === 0) {
            r = g = b = l; // achromatic
        } else {
            const hue2rgb = (p: number, q: number, t: number) => {
                if (t < 0) t += 1;
                if (t > 1) t -= 1;
                if (t < 1 / 6) return p + (q - p) * 6 * t;
                if (t < 1 / 2) return q;
                if (t < 2 / 3) return p + (q - p) * (2 / 3 - t) * 6;
                return p;
            };

            const q = l < 0.5 ? l * (1 + s) : l + s - l * s;
            const p = 2 * l - q;
            r = hue2rgb(p, q, h + 1 / 3);
            g = hue2rgb(p, q, h);
            b = hue2rgb(p, q, h - 1 / 3);
        }

        return {
            r: Math.round(r * 255),
            g: Math.round(g * 255),
            b: Math.round(b * 255),
        };
    }

    private rgbToHex(r: number, g: number, b: number): string {
        return (
            '#' +
            [r, g, b]
                .map(x => x.toString(16).padStart(2, '0'))
                .join('')
                .toUpperCase()
        );
    }
}
