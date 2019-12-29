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

import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
    selector: 'dashboard-status-filled',
    templateUrl: './dashboard-status-filled.component.html',
    styleUrls: ['./dashboard-status-filled.component.css']
})
export class DashboardStatusFilledComponent {

    @Input() color: string = "rgb(156, 156, 156)";
    _label: string;
    _statusValue: string;

    chartData: any;

    constructor() {

    }

    ngOnInit() {

    }

    @Input()
    set statusValue(statusValue: string) {
        this._statusValue = statusValue;
        this.updateChartData();
    }

    @Input()
    set label(label: string) {
        this._label = label;
        this.updateChartData();
    }

    updateChartData() {
        this.chartData = [];
        this.chartData = [{"name": this._label, "value": this._statusValue}];
    }

    getBackground() {
        return {'background': this.color};
    }


}