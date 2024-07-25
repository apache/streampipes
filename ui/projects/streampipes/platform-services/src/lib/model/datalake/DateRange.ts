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

export interface TimeSettings {
    startTime: number;
    endTime: number;
    // deprecated
    dynamicSelection?: 15 | 60 | 1440 | 10080 | 43800 | 525600 | -1;
    timeSelectionId?: TimeSelectionId;
}

export interface TimeString {
    startDate: string;
    startTime: string;
    endDate: string;
    endTime: string;
}

export enum TimeSelectionId {
    CUSTOM,
    LAST_15_MINUTES,
    LAST_HOUR,
    CURRENT_HOUR,
    LAST_DAY,
    CURRENT_DAY,
    LAST_WEEK,
    CURRENT_WEEK,
    LAST_MONTH,
    CURRENT_MONTH,
    LAST_YEAR,
    CURRENT_YEAR,
}

export interface QuickTimeSelection {
    label: string;
    timeSelectionId: TimeSelectionId;
    startTime: (now: Date) => Date;
    endTime: (now: Date) => Date;
    addDividerAfter?: boolean;
}

export class DateRange {
    public startDate: Date;
    public endDate: Date;

    constructor(startDate?: Date, endDate?: Date) {
        if (startDate && endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }

    static fromTimeSettings(timeSettings: TimeSettings): DateRange {
        const range = new DateRange();
        range.startDate = new Date(timeSettings.startTime);
        range.endDate = new Date(timeSettings.endTime);
        return range;
    }
}
