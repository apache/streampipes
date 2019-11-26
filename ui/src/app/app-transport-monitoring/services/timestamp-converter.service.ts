/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import {Injectable} from "@angular/core";

@Injectable()
export class TimestampConverterService {

    constructor() {

    }

    convertTimestamp(timestamp: number): string {
        return "";
    }

    convertTimestampHoursOnly(timestamp: number): string {
        var date = new Date(timestamp);
        var hours = date.getHours();
        var minutes = "0" + date.getMinutes();
        var seconds = "0" + date.getSeconds();

        return hours + ':' + minutes.substr(-2) + ':' + seconds.substr(-2);
    }

    dateDiffHoursOnly(startTimestamp: number, endTimestamp: number):string {
        var startDate = new Date(startTimestamp);
        var endDate = new Date(endTimestamp);

        var hoursDiff = endDate.getHours() - startDate.getHours();
        var minutesDiff = "0" + (endDate.getMinutes() - startDate.getMinutes());
        var secondsDiff = "0" + (endDate.getSeconds() - startDate.getSeconds());

        return hoursDiff + ':' + minutesDiff.substr(-2) + ':' + secondsDiff.substr(-2);
    }
}