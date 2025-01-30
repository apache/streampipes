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
import { DataExplorerWidgetModel } from '@streampipes/platform-services';
import { DataExplorerChartRegistry } from '../../../../../../data-explorer-shared/registry/data-explorer-chart-registry';

@Component({
    selector: 'sp-chart-preview',
    templateUrl: './chart-preview.component.html',
    styleUrls: ['./chart-preview.component.scss'],
})
export class ChartPreviewComponent implements OnInit {
    @Input()
    chart: DataExplorerWidgetModel;

    widgetTypeLabel: string;

    @Output()
    addChartEmitter: EventEmitter<string> = new EventEmitter<string>();

    constructor(private widgetRegistryService: DataExplorerChartRegistry) {}

    ngOnInit() {
        this.widgetTypeLabel = this.widgetRegistryService.getChartTemplate(
            this.chart.widgetType,
        ).label;
    }
}
