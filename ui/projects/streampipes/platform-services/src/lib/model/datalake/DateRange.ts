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
    timeSelectionId?: string;
}

export interface ExtendedTimeSettings {
    timeSettings: TimeSettings;
    supportsLiveRefresh: boolean;
}

export class TimeSelectionConstants {
    static CUSTOM = 'custom';
    static LAST_15_MINUTES = 'last-15-minutes';
    static LAST_HOUR = 'last-hour';
    static CURRENT_HOUR = 'current-hour';
    static LAST_DAY = 'last-day';
    static CURRENT_DAY = 'current-day';
    static LAST_WEEK = 'last-week';
    static CURRENT_WEEK = 'current-week';
    static LAST_MONTH = 'last-month';
    static CURRENT_MONTH = 'current-month';
    static LAST_YEAR = 'last-year';
    static CURRENT_YEAR = 'current-year';

    static getLegacyTimeSelectionID(legacyID: number) {
        if (legacyID === 0) {
            return TimeSelectionConstants.CUSTOM;
        } else if (legacyID === 1) {
            return TimeSelectionConstants.LAST_15_MINUTES;
        } else if (legacyID === 2) {
            return TimeSelectionConstants.LAST_HOUR;
        } else if (legacyID === 3) {
            return TimeSelectionConstants.CURRENT_HOUR;
        } else if (legacyID === 4) {
            return TimeSelectionConstants.LAST_DAY;
        } else if (legacyID === 5) {
            return TimeSelectionConstants.CURRENT_DAY;
        } else if (legacyID === 6) {
            return TimeSelectionConstants.LAST_WEEK;
        } else if (legacyID === 7) {
            return TimeSelectionConstants.CURRENT_WEEK;
        } else if (legacyID === 8) {
            return TimeSelectionConstants.LAST_MONTH;
        } else if (legacyID === 9) {
            return TimeSelectionConstants.CURRENT_MONTH;
        } else if (legacyID === 10) {
            return TimeSelectionConstants.LAST_YEAR;
        } else if (legacyID === 11) {
            return TimeSelectionConstants.CURRENT_YEAR;
        }
    }
}

export interface WidgetTimeSettings {
    timeSettings: TimeSettings;
    widgetIndex?: number;
}

export interface TimeString {
    startDate: string;
    startTime: string;
    endDate: string;
    endTime: string;
    sameDay: boolean;
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
    timeSelectionId: string;
    startTime: (now: Date) => Date;
    endTime: (now: Date) => Date;
    addDividerAfter?: boolean;
    supportsLiveRefresh?: boolean;
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
