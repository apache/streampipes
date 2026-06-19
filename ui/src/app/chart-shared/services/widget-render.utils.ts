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

export function clampValue(value: number, min: number, max: number): number {
    return Math.min(Math.max(value, min), max);
}

export function normalizeWidgetDecimals(decimals: unknown): number | undefined {
    if (decimals === null || decimals === undefined || decimals === '') {
        return undefined;
    }

    const parsedValue = Number(decimals);
    if (!Number.isFinite(parsedValue)) {
        return undefined;
    }

    return Math.min(10, Math.max(0, Math.round(parsedValue)));
}

export function formatWidgetNumber(
    value: number,
    locale: string,
    decimals: unknown,
): string {
    const normalizedDecimals = normalizeWidgetDecimals(decimals);

    return new Intl.NumberFormat(locale, {
        maximumFractionDigits: normalizedDecimals ?? 20,
        minimumFractionDigits: normalizedDecimals ?? 0,
    }).format(value);
}

export function scaleResponsiveValue(
    min: number,
    max: number,
    scale: number,
    scaleMin: number,
    scaleMax: number,
): number {
    const normalizedScale = (scale - scaleMin) / (scaleMax - scaleMin);
    const scaledValue = min + (max - min) * normalizedScale;
    return Math.round(clampValue(scaledValue, min, max));
}

export function resolveResponsiveFontSize(
    manualSize: number | undefined,
    min: number,
    max: number,
    scale: number,
    scaleMin: number,
    scaleMax: number,
): number {
    if (
        manualSize === undefined ||
        manualSize === null ||
        Number.isNaN(Number(manualSize)) ||
        manualSize <= 0
    ) {
        return scaleResponsiveValue(min, max, scale, scaleMin, scaleMax);
    }

    return Math.round(Math.max(1, manualSize) * scale * 10) / 10;
}
