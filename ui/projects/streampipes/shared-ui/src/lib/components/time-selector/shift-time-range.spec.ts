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
import { endOfDay } from 'date-fns';
import { shiftTimeRange } from './shift-time-range';

const day = (month: number, date: number) =>
    new Date(2026, month - 1, date).getTime();
const range = (month: number, start: number, end = start) => ({
    startTime: day(month, start),
    endTime: endOfDay(day(month, end)).getTime(),
});

describe('shiftTimeRange', () => {
    it.each([
        [10, 25, -1, 24],
        [10, 24, 1, 25],
        [3, 29, -1, 28],
        [3, 28, 1, 29],
        [10, 20, -1, 19],
    ] as const)(
        'moves calendar day %i/%i in direction %i',
        (month, date, direction, expected) => {
            const current = range(month, date);
            const shifted = shiftTimeRange(
                current.startTime,
                current.endTime,
                direction,
            );
            expect(shifted).toEqual(range(month, expected));
            expect(
                shiftTimeRange(
                    shifted.startTime,
                    shifted.endTime,
                    direction === 1 ? -1 : 1,
                ),
            ).toEqual(current);
        },
    );

    it('preserves the number of calendar days for multi-day selections', () => {
        const current = range(10, 24, 26);
        expect(shiftTimeRange(current.startTime, current.endTime, -1)).toEqual(
            range(10, 21, 23),
        );
    });

    it('preserves exact inclusive duration for selections with times', () => {
        const start = new Date(2026, 9, 25, 6).getTime();
        const end = new Date(2026, 9, 25, 12).getTime();
        const duration = end - start + 1;
        expect(shiftTimeRange(start, end, -1)).toEqual({
            startTime: start - duration,
            endTime: end - duration,
        });
        expect(shiftTimeRange(start, end, 1)).toEqual({
            startTime: start + duration,
            endTime: end + duration,
        });
    });
});
