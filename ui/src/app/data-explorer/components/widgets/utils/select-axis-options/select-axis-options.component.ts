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

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { DataExplorerField } from '@streampipes/platform-services';
import { TimeSeriesChartWidgetModel } from '../../time-series-chart/model/time-series-chart-widget.model';
import { WidgetConfigurationService } from '../../../../services/widget-configuration.service';
import { AxisConfig } from '../../../../models/dataview-dashboard.model';

@Component({
    selector: 'sp-select-axis-options',
    templateUrl: './select-axis-options.component.html',
    styleUrls: ['./select-axis-options.component.scss'],
})
export class SpSelectAxisOptionsComponent {
    @Input() title: string;
    @Input() axisConfig: AxisConfig;

    @Output() viewRefreshEmitter: EventEmitter<void> = new EventEmitter<void>();

    constructor() {}
}
