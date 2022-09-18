/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import { Component, Input, OnInit } from '@angular/core';
import { DataExplorerDataConfig, DateRange } from '@streampipes/platform-services';

@Component({
  selector: 'sp-select-data-range',
  templateUrl: './select-data-range.component.html',
  styleUrls: ['./select-data-range.component.scss']
})
export class SelectDataRangeComponent implements OnInit {
  @Input() date: DateRange;
  @Input() dataConfig: DataExplorerDataConfig;
  @Input() dataRangeConfiguration: 'all' | 'customInterval' | 'visible';

  dateRange: Date[] = []; // [0] start, [1] end

  ngOnInit(): void {
    if (!this.date) {
      const endDate = new Date();

      endDate.setDate(endDate.getDate() - 5);
      this.date = {startDate: new Date(), endDate};
    }
    this.dateRange[0] = this.date.startDate;
    this.dateRange[1] = this.date.endDate;

    if (this.dataConfig) {
      this.dataRangeConfiguration = 'visible';
    }
  }
}
