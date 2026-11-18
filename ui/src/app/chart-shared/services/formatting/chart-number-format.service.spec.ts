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
import {
    ChartNumberFormatService,
    normalizeDecimals,
} from './chart-number-format.service';

describe('ChartNumberFormatService', () => {
    let service: ChartNumberFormatService;
    let currentLang = 'en';

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
        service = TestBed.inject(ChartNumberFormatService);
    });

    describe('automatic formatting', () => {
        it('limits values to two fraction digits', () => {
            expect(service.format(39.76818268646499)).toBe('39.77');
        });

        it('drops trailing zeros of integral values', () => {
            expect(service.format(182)).toBe('182');
            expect(service.format(182.0)).toBe('182');
        });

        it('keeps three significant digits for small values', () => {
            expect(service.format(0.012345)).toBe('0.0123');
            expect(service.format(-0.5)).toBe('-0.5');
        });

        it('formats zero as a plain zero', () => {
            expect(service.format(0)).toBe('0');
        });

        it('uses the current UI language for separators', () => {
            currentLang = 'de';
            expect(service.format(1234.5)).toBe('1.234,5');
        });
    });

    describe('explicit decimals', () => {
        it('renders exactly the configured fraction digits', () => {
            expect(service.format(3, { decimals: 2 })).toBe('3.00');
            expect(service.format(3.14159, { decimals: 1 })).toBe('3.1');
        });

        it('ignores fraction digits for integer fields', () => {
            expect(service.format(182, { decimals: 2, integer: true })).toBe(
                '182',
            );
            expect(service.format(182.6, { integer: true })).toBe('183');
        });
    });

    describe('raw mode', () => {
        it('returns the stored value unchanged', () => {
            expect(service.format(39.76818268646499, { raw: true })).toBe(
                '39.76818268646499',
            );
            expect(service.format('12.50', { raw: true })).toBe('12.50');
        });
    });

    describe('non-numeric input', () => {
        it('returns an empty string for missing values', () => {
            expect(service.format(null)).toBe('');
            expect(service.format(undefined)).toBe('');
            expect(service.format('')).toBe('');
        });

        it('returns text values as they are', () => {
            expect(service.format('RUNNING')).toBe('RUNNING');
        });

        it('parses numeric strings', () => {
            expect(service.format('12.345')).toBe('12.35');
        });
    });

    describe('percentages', () => {
        it('formats ratios with one fraction digit by default', () => {
            expect(service.formatPercent(0.4932)).toBe('49.3%');
        });

        it('formats whole percentages without fraction digits', () => {
            expect(service.formatPercent(1)).toBe('100%');
        });

        it('honours explicit decimals', () => {
            expect(service.formatPercent(0.5, { decimals: 2 })).toBe('50.00%');
        });
    });

    describe('resolveOptions', () => {
        it('resolves automatic formatting for configurations without numberFormat', () => {
            expect(service.resolveOptions(undefined)).toEqual({
                decimals: undefined,
                raw: false,
                integer: false,
            });
        });

        it('reads decimals and raw mode from the appearance configuration', () => {
            expect(
                service.resolveOptions({
                    backgroundColor: '',
                    textColor: '',
                    widgetTitle: '',
                    numberFormat: { decimals: 3, rawValues: true },
                }),
            ).toEqual({ decimals: 3, raw: true, integer: false });
        });

        it('marks integer fields and count aggregations', () => {
            const characteristics = {
                dimension: false,
                numeric: true,
                binary: false,
                semanticTypes: [],
            };
            expect(
                service.resolveOptions(undefined, {
                    fieldCharacteristics: { ...characteristics, integer: true },
                }).integer,
            ).toBe(true);
            expect(
                service.resolveOptions(undefined, {
                    fieldCharacteristics: characteristics,
                    aggregation: 'COUNT',
                }).integer,
            ).toBe(true);
            expect(
                service.resolveOptions(undefined, {
                    fieldCharacteristics: characteristics,
                    aggregation: 'MEAN',
                }).integer,
            ).toBe(false);
        });
    });

    describe('normalizeDecimals', () => {
        it('clamps and rounds valid input', () => {
            expect(normalizeDecimals(2)).toBe(2);
            expect(normalizeDecimals('4')).toBe(4);
            expect(normalizeDecimals(2.6)).toBe(3);
            expect(normalizeDecimals(-1)).toBe(0);
            expect(normalizeDecimals(42)).toBe(10);
        });

        it('treats empty or invalid input as unset', () => {
            expect(normalizeDecimals(undefined)).toBeUndefined();
            expect(normalizeDecimals(null)).toBeUndefined();
            expect(normalizeDecimals('')).toBeUndefined();
            expect(normalizeDecimals('abc')).toBeUndefined();
        });
    });
});
