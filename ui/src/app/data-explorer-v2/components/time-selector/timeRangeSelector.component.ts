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

import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { DateRange } from '../../../core-model/datalake/DateRange';

@Component({
  selector: 'sp-time-range-selector',
  templateUrl: 'timeRangeSelector.component.html',
  styleUrls: ['./timeRangeSelector.component.css']
})
export class TimeRangeSelectorComponent implements OnInit {

  @Output()
  dateRangeEmitter = new EventEmitter<DateRange>();

  public dateRange: DateRange;

  public possibleTimeButtons = [
    {value: '15 min', offset: 15},
    {value: '1 hour', offset: 60},
    {value: '1 day', offset: 1440},
    {value: '1 week', offset: 10080},
    {value: '1 month', offset: 43800},
    {value: '1 year', offset: 525600},
    {value: 'custom', offset: -1},
    ];

  public selectedTimeButton;

  constructor() {
  }

  ngOnInit() {
    this.selectedTimeButton = this.possibleTimeButtons[0];
    this.setCurrentDateRange(this.selectedTimeButton);
  }

  reloadData() {
    this.dateRangeEmitter.emit(this.dateRange);
  }

  increaseTime() {
    this.changeTimeByInterval((a, b) => a + b);
  }

  decreaseTime() {
    this.changeTimeByInterval((a, b) => a - b);
  }

  private  changeTimeByInterval(func) {
    const difference = this.dateRange.endDate.getTime() - this.dateRange.startDate.getTime();
    const newStartTime = new Date(func(this.dateRange.startDate.getTime(), difference));
    const newEndTime = new Date(func(this.dateRange.endDate.getTime(), difference));

    this.dateRange = new DateRange(newStartTime, newEndTime);
    this.selectedTimeButton =  this.possibleTimeButtons[this.possibleTimeButtons.length - 1];
    this.reloadData();
  }

  changeCustomDateRange() {
    this.selectedTimeButton =  this.possibleTimeButtons[this.possibleTimeButtons.length - 1];
    const newStartTime = new Date(this.dateRange.startDate.getTime());
    const newEndTime = new Date(this.dateRange.endDate.getTime());

    this.dateRange = new DateRange(newStartTime, newEndTime);
    this.reloadData();
  }

  /**
   * Sets the current date range from now to the value of offset in the past
   * @param offset in minutes
   */
  setCurrentDateRange(item) {
    this.selectedTimeButton = item;
    const current = new Date();
    this.dateRange = new DateRange(new Date(current.getTime() - item.offset * 60000), current);
    this.reloadData();
  }
}
