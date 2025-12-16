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

type Rgb = { r: number; g: number; b: number; a: number };

@Injectable({ providedIn: 'root' })
export class SpColorizationService {
    generateRandomColor(): string {
        // Hue: full color wheel
        const h = Math.floor(Math.random() * 360);

        // Saturation: avoid dull or neon colors
        const s = 55 + Math.random() * 25; // 55–80%

        // Lightness: avoid too dark or too light
        const l = 40 + Math.random() * 25; // 40–65%

        const { r, g, b } = this.hslToRgb(h, s / 100, l / 100);
        return this.rgbToHex({ r, g, b, a: 1 });
    }

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
        return this.rgbToHex({ r: tr, g: tg, b: tb, a: 1 });
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

    /**
     * Mixes two colors in sRGB.
     * weightB: 0..1 (0 => all colorA, 1 => all colorB)
     */
    mix(colorA: string, colorB: string, weightB: number): string {
        const a = this.parseColor(colorA);
        const b = this.parseColor(colorB);
        if (!a || !b) return colorA;

        const t = this.clamp01(weightB);

        const out: Rgb = {
            r: Math.round(a.r + (b.r - a.r) * t),
            g: Math.round(a.g + (b.g - a.g) * t),
            b: Math.round(a.b + (b.b - a.b) * t),
            a: a.a + (b.a - a.a) * t,
        };

        // Output as hex (ignore alpha for simplicity; you can output rgba if you prefer)
        return this.rgbToHex(out);
    }

    /**
     * Ensures fg has at least minRatio contrast against bg.
     * If it doesn't, it will iteratively push fg toward black or white,
     * whichever improves contrast faster.
     */
    ensureContrast(fg: string, bg: string, minRatio: number = 4.5): string {
        const fgRgb = this.parseColor(fg);
        const bgRgb = this.parseColor(bg);

        if (!fgRgb || !bgRgb) return fg;

        // Flatten both on white if they have alpha
        const bgEff = this.flattenOn(bgRgb, { r: 255, g: 255, b: 255, a: 1 });
        let fgEff = this.flattenOn(fgRgb, { r: 255, g: 255, b: 255, a: 1 });

        let current = this.contrastRatio(fgEff, bgEff);
        if (current >= minRatio) return this.rgbToHex(fgEff);

        const towardBlack = { r: 0, g: 0, b: 0, a: 1 };
        const towardWhite = { r: 255, g: 255, b: 255, a: 1 };

        // Decide direction by which gives higher contrast if we move a bit
        const step = 0.08;
        const testBlack = this.mixRgb(fgEff, towardBlack, step);
        const testWhite = this.mixRgb(fgEff, towardWhite, step);

        const cBlack = this.contrastRatio(testBlack, bgEff);
        const cWhite = this.contrastRatio(testWhite, bgEff);

        const target = cBlack >= cWhite ? towardBlack : towardWhite;

        // Iteratively move fg towards target until we meet contrast or hit limit
        let t = step;
        let best = fgEff;
        let bestC = current;

        for (let i = 0; i < 20; i++) {
            const candidate = this.mixRgb(fgEff, target, t);
            const c = this.contrastRatio(candidate, bgEff);
            if (c > bestC) {
                bestC = c;
                best = candidate;
            }
            if (c >= minRatio) return this.rgbToHex(candidate);
            t = Math.min(1, t + step);
        }

        // Return best effort if we couldn't reach the desired ratio
        return this.rgbToHex(best);
    }

    // -----------------------
    // Helpers
    // -----------------------

    private parseColor(input: string): Rgb | null {
        if (!input) return null;
        const s = input.trim().toLowerCase();

        // Can't resolve CSS variables without DOM access
        if (s.startsWith('var(')) return null;

        if (s === 'black') return { r: 0, g: 0, b: 0, a: 1 };
        if (s === 'white') return { r: 255, g: 255, b: 255, a: 1 };
        if (s === 'transparent') return { r: 0, g: 0, b: 0, a: 0 };

        // #RGB, #RRGGBB, #RRGGBBAA
        if (s.startsWith('#')) {
            const hex = s.slice(1);
            if (hex.length === 3) {
                const r = parseInt(hex[0] + hex[0], 16);
                const g = parseInt(hex[1] + hex[1], 16);
                const b = parseInt(hex[2] + hex[2], 16);
                return { r, g, b, a: 1 };
            }
            if (hex.length === 6) {
                const r = parseInt(hex.slice(0, 2), 16);
                const g = parseInt(hex.slice(2, 4), 16);
                const b = parseInt(hex.slice(4, 6), 16);
                return { r, g, b, a: 1 };
            }
            if (hex.length === 8) {
                const r = parseInt(hex.slice(0, 2), 16);
                const g = parseInt(hex.slice(2, 4), 16);
                const b = parseInt(hex.slice(4, 6), 16);
                const a = parseInt(hex.slice(6, 8), 16) / 255;
                return { r, g, b, a: this.clamp01(a) };
            }
            return null;
        }

        // rgb() / rgba()
        const rgbMatch = s.match(
            /^rgba?\(\s*([0-9.]+)\s*,\s*([0-9.]+)\s*,\s*([0-9.]+)\s*(?:,\s*([0-9.]+)\s*)?\)$/,
        );
        if (rgbMatch) {
            const r = this.clamp255(Number(rgbMatch[1]));
            const g = this.clamp255(Number(rgbMatch[2]));
            const b = this.clamp255(Number(rgbMatch[3]));
            const a =
                rgbMatch[4] !== undefined
                    ? this.clamp01(Number(rgbMatch[4]))
                    : 1;
            return { r, g, b, a };
        }

        return null;
    }

    private rgbToHex(rgb: Rgb): string {
        const r = this.clamp255(rgb.r).toString(16).padStart(2, '0');
        const g = this.clamp255(rgb.g).toString(16).padStart(2, '0');
        const b = this.clamp255(rgb.b).toString(16).padStart(2, '0');
        return `#${r}${g}${b}`;
    }

    private mixRgb(a: Rgb, b: Rgb, t: number): Rgb {
        const w = this.clamp01(t);
        return {
            r: Math.round(a.r + (b.r - a.r) * w),
            g: Math.round(a.g + (b.g - a.g) * w),
            b: Math.round(a.b + (b.b - a.b) * w),
            a: a.a + (b.a - a.a) * w,
        };
    }

    /**
     * Alpha-composite fg over bg.
     */
    private flattenOn(fg: Rgb, bg: Rgb): Rgb {
        const a = this.clamp01(fg.a);
        const inv = 1 - a;
        return {
            r: Math.round(fg.r * a + bg.r * inv),
            g: Math.round(fg.g * a + bg.g * inv),
            b: Math.round(fg.b * a + bg.b * inv),
            a: 1,
        };
    }

    private contrastRatio(c1: Rgb, c2: Rgb): number {
        const L1 = this.relativeLuminance(c1);
        const L2 = this.relativeLuminance(c2);
        const lighter = Math.max(L1, L2);
        const darker = Math.min(L1, L2);
        return (lighter + 0.05) / (darker + 0.05);
    }

    /**
     * WCAG relative luminance in sRGB
     */
    private relativeLuminance(c: Rgb): number {
        const rs = this.srgbToLinear(c.r / 255);
        const gs = this.srgbToLinear(c.g / 255);
        const bs = this.srgbToLinear(c.b / 255);
        return 0.2126 * rs + 0.7152 * gs + 0.0722 * bs;
    }

    private srgbToLinear(x: number): number {
        return x <= 0.04045 ? x / 12.92 : Math.pow((x + 0.055) / 1.055, 2.4);
    }

    private clamp255(v: number): number {
        if (Number.isNaN(v)) return 0;
        return Math.max(0, Math.min(255, Math.round(v)));
    }

    private clamp01(v: number): number {
        if (Number.isNaN(v)) return 0;
        return Math.max(0, Math.min(1, v));
    }
}
