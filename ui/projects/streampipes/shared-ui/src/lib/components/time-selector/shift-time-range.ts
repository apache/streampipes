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

import {
    addDays,
    differenceInCalendarDays,
    endOfDay,
    startOfDay,
} from 'date-fns';

/** Move full-day ranges by calendar days, including across daylight-saving changes. */
export function shiftTimeRange(
    startTime: number,
    endTime: number,
    direction: -1 | 1,
): { startTime: number; endTime: number } {
    if (
        startOfDay(startTime).getTime() === startTime &&
        endOfDay(endTime).getTime() === endTime
    ) {
        const days = differenceInCalendarDays(endTime, startTime) + 1;
        return {
            startTime: startOfDay(
                addDays(startTime, direction * days),
            ).getTime(),
            endTime: endOfDay(addDays(endTime, direction * days)).getTime(),
        };
    }
    const offset = direction * (endTime - startTime + 1);
    return { startTime: startTime + offset, endTime: endTime + offset };
}
