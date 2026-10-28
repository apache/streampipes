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
import { TranslateService } from '@ngx-translate/core';

export type TimestampInput = number | string | Date | null | undefined;

/** How much detail a timestamp needs, derived from the visible time span. */
export type TimeResolution =
    'milliseconds' | 'seconds' | 'minutes' | 'date-time' | 'date';

const ONE_SECOND = 1000;
const ONE_MINUTE = 60 * ONE_SECOND;
const ONE_DAY = 24 * 60 * ONE_MINUTE;
const ONE_WEEK = 7 * ONE_DAY;

/**
 * Single place that turns timestamps into display strings for charts and
 * widgets (table cells, tooltips, heatmap axes, value cards, empty states).
 *
 * The resolution follows the visible span: within a minute milliseconds are
 * shown, within a day only the time of day, beyond that date and time, so a
 * live dashboard reads "17:03:12" while a yearly view reads "10/28/2026 17:03".
 */
@Injectable({ providedIn: 'root' })
export class ChartTimeFormatService {
    private translateService = inject(TranslateService);
    private formatters = new Map<string, Intl.DateTimeFormat>();

    /** Formats one timestamp with the resolution matching the given span. */
    formatTimestamp(
        value: TimestampInput,
        spanMs?: number,
        resolution: TimeResolution = this.resolutionForSpan(spanMs),
    ): string {
        const date = this.toDate(value);
        if (!date) {
            return value === null || value === undefined ? '' : String(value);
        }
        return this.getFormatter(resolution).format(date);
    }

    /** Formats a list of timestamps with one common resolution (axis labels). */
    formatTimestamps(values: TimestampInput[]): string[] {
        const resolution = this.resolutionForSpan(this.spanOf(values));
        return values.map(value =>
            this.formatTimestamp(value, undefined, resolution),
        );
    }

    /** Formats a start/end pair as one range, e.g. for empty states. */
    formatRange(start: TimestampInput, end: TimestampInput): string {
        const startDate = this.toDate(start);
        const endDate = this.toDate(end);
        if (!startDate || !endDate) {
            return '';
        }
        const sameDay = startDate.toDateString() === endDate.toDateString();
        const startText = this.getFormatter('date-time').format(startDate);
        const endText = this.getFormatter(
            sameDay ? 'minutes' : 'date-time',
        ).format(endDate);
        return `${startText} – ${endText}`;
    }

    /** The span (max - min) of the valid timestamps in the list. */
    spanOf(values: TimestampInput[]): number | undefined {
        let min = Number.POSITIVE_INFINITY;
        let max = Number.NEGATIVE_INFINITY;
        values.forEach(value => {
            const time = this.toDate(value)?.getTime();
            if (time !== undefined) {
                min = Math.min(min, time);
                max = Math.max(max, time);
            }
        });
        return max >= min ? max - min : undefined;
    }

    resolutionForSpan(spanMs: number | undefined): TimeResolution {
        if (spanMs === undefined || !Number.isFinite(spanMs)) {
            return 'date-time';
        }
        if (spanMs < ONE_MINUTE) {
            return 'milliseconds';
        }
        if (spanMs < ONE_DAY) {
            return 'seconds';
        }
        if (spanMs < ONE_WEEK) {
            return 'date-time';
        }
        return 'date';
    }

    toDate(value: TimestampInput): Date | undefined {
        if (value === null || value === undefined || value === '') {
            return undefined;
        }
        const date = value instanceof Date ? value : new Date(value);
        return Number.isNaN(date.getTime()) ? undefined : date;
    }

    private getFormatter(resolution: TimeResolution): Intl.DateTimeFormat {
        const locale = this.currentLocale;
        const key = `${locale}|${resolution}`;
        let formatter = this.formatters.get(key);
        if (!formatter) {
            formatter = new Intl.DateTimeFormat(
                locale,
                this.toIntlOptions(resolution),
            );
            this.formatters.set(key, formatter);
        }
        return formatter;
    }

    private toIntlOptions(
        resolution: TimeResolution,
    ): Intl.DateTimeFormatOptions {
        const time: Intl.DateTimeFormatOptions = {
            hour: '2-digit',
            minute: '2-digit',
            hourCycle: 'h23',
        };
        const date: Intl.DateTimeFormatOptions = {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
        };
        switch (resolution) {
            case 'milliseconds':
                return {
                    ...time,
                    second: '2-digit',
                    fractionalSecondDigits: 3,
                };
            case 'seconds':
                return { ...time, second: '2-digit' };
            case 'minutes':
                return time;
            case 'date':
                return date;
            default:
                return { ...date, ...time };
        }
    }

    private get currentLocale(): string {
        return (
            this.translateService.getCurrentLang() ||
            this.translateService.getFallbackLang() ||
            'en'
        );
    }
}
