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

import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { TranslateService } from '@ngx-translate/core';
import { ChartTimeFormatService } from './chart-time-format.service';

const MINUTE = 60 * 1000;
const HOUR = 60 * MINUTE;
const DAY = 24 * HOUR;

describe('ChartTimeFormatService', () => {
    let service: ChartTimeFormatService;
    let currentLang = 'en';
    // 2026-10-28 16:44:55.938 local time
    const timestamp = new Date(2026, 9, 28, 16, 44, 55, 938).getTime();

    beforeEach(() => {
        currentLang = 'en';
        TestBed.configureTestingModule({
            providers: [
                {
                    provide: TranslateService,
                    useValue: {
                        getCurrentLang: () => currentLang,
                        getFallbackLang: () => 'en',
                    },
                },
            ],
        });
        service = TestBed.inject(ChartTimeFormatService);
    });

    describe('resolutionForSpan', () => {
        it('picks the detail level from the visible span', () => {
            expect(service.resolutionForSpan(30 * 1000)).toBe('milliseconds');
            expect(service.resolutionForSpan(15 * MINUTE)).toBe('seconds');
            expect(service.resolutionForSpan(3 * DAY)).toBe('date-time');
            expect(service.resolutionForSpan(30 * DAY)).toBe('date');
        });

        it('falls back to date and time when the span is unknown', () => {
            expect(service.resolutionForSpan(undefined)).toBe('date-time');
            expect(service.resolutionForSpan(Number.NaN)).toBe('date-time');
        });
    });

    describe('formatTimestamp', () => {
        it('shows the time of day only within a day', () => {
            expect(service.formatTimestamp(timestamp, 15 * MINUTE)).toBe(
                '16:44:55',
            );
        });

        it('shows milliseconds within a minute', () => {
            expect(service.formatTimestamp(timestamp, 20 * 1000)).toBe(
                '16:44:55.938',
            );
        });

        it('shows date and time without a span', () => {
            expect(service.formatTimestamp(timestamp)).toBe(
                '10/28/2026, 16:44',
            );
        });

        it('accepts ISO strings and Date objects', () => {
            const iso = new Date(timestamp).toISOString();
            expect(service.formatTimestamp(iso, HOUR)).toBe('16:44:55');
            expect(service.formatTimestamp(new Date(timestamp), HOUR)).toBe(
                '16:44:55',
            );
        });

        it('passes through values that are not timestamps', () => {
            expect(service.formatTimestamp('n/a')).toBe('n/a');
            expect(service.formatTimestamp(null)).toBe('');
            expect(service.formatTimestamp(undefined)).toBe('');
        });

        it('uses the current UI language', () => {
            currentLang = 'de';
            expect(service.formatTimestamp(timestamp)).toBe(
                '28.10.2026, 16:44',
            );
        });
    });

    describe('formatTimestamps', () => {
        it('uses one resolution for the whole list', () => {
            const labels = service.formatTimestamps([
                timestamp,
                timestamp + 5 * MINUTE,
                timestamp + 10 * MINUTE,
            ]);
            expect(labels).toEqual(['16:44:55', '16:49:55', '16:54:55']);
        });

        it('ignores entries that are not timestamps when deriving the span', () => {
            const labels = service.formatTimestamps([
                'time',
                timestamp,
                timestamp + MINUTE,
            ]);
            expect(labels).toEqual(['time', '16:44:55', '16:45:55']);
        });
    });

    describe('formatRange', () => {
        it('repeats the date only when the range spans several days', () => {
            expect(
                service.formatRange(timestamp, timestamp + 15 * MINUTE),
            ).toBe('10/28/2026, 16:44 – 16:59');
            expect(service.formatRange(timestamp, timestamp + 2 * DAY)).toBe(
                '10/28/2026, 16:44 – 10/30/2026, 16:44',
            );
        });

        it('returns an empty string for incomplete ranges', () => {
            expect(service.formatRange(undefined, timestamp)).toBe('');
        });
    });

    describe('spanOf', () => {
        it('returns the difference between the earliest and latest timestamp', () => {
            expect(
                service.spanOf([timestamp + HOUR, timestamp, timestamp + DAY]),
            ).toBe(DAY);
        });

        it('returns undefined without valid timestamps', () => {
            expect(service.spanOf(['time', null])).toBeUndefined();
        });
    });
});
