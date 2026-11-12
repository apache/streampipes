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

import { describe, expect, it } from 'vitest';
import {
    formatPreviewValue,
    formatTimestamp,
    orderFields,
} from './live-preview-format';

describe('live preview formatting', () => {
    it.each([
        [undefined, '—'],
        [null, '—'],
        [42, '42'],
        [true, 'true'],
        ['flowrate01', 'flowrate01'],
        [{ a: 1 }, '{"a":1}'],
    ])('formats %s', (value, expected) => {
        expect(formatPreviewValue(value)).toBe(expected);
    });

    it('limits decimals instead of showing raw floats', () => {
        expect(formatPreviewValue(43.23667119070548)).toMatch(/^43[.,]237$/);
    });

    it('formats timestamps of today as a time with milliseconds', () => {
        const now = new Date(2026, 10, 8, 17, 58, 16, 443).getTime();

        expect(formatTimestamp(now, now)).toMatch(/58:16\.443$/);
        expect(formatPreviewValue(now, true)).not.toBe(String(now));
    });

    it('prefixes the date for timestamps of other days', () => {
        const event = new Date(2026, 10, 1, 8, 0, 0, 5).getTime();
        const now = new Date(2026, 10, 8, 8, 0).getTime();

        expect(formatTimestamp(event, now)).toContain(
            new Date(event).toLocaleDateString(),
        );
    });

    it('orders schema fields first and appends unknown event fields', () => {
        const fields = orderFields(
            [
                { runtimeName: 'timestamp', timestamp: true },
                { runtimeName: 'temperature', timestamp: false },
            ],
            [{ temperature: 1, zeta: 1, alpha: 2 }, undefined],
        );

        expect(fields.map(f => f.runtimeName)).toEqual([
            'timestamp',
            'temperature',
            'alpha',
            'zeta',
        ]);
    });
});
