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

import { Component, Input, OnInit } from '@angular/core';
import { WidgetEchartsAppearanceConfig } from '../../../../models/dataview-dashboard.model';
import { WidgetConfigurationService } from '../../../../services/widget-configuration.service';

@Component({
    selector: 'sp-echarts-widget-appearance-config',
    templateUrl: './echarts-widget-appearance-config.component.html',
})
export class SpEchartsWidgetAppearanceConfigComponent implements OnInit {
    @Input()
    appearanceConfig: WidgetEchartsAppearanceConfig;

    constructor(
        private widgetConfigurationService: WidgetConfigurationService,
    ) {}

    ngOnInit() {
        this.appearanceConfig.chartAppearance ??= {
            showLegend: true,
            showToolbox: true,
            showTooltip: true,
        };
    }

    triggerViewUpdate() {
        this.widgetConfigurationService.notify({
            refreshView: true,
            refreshData: false,
        });
    }
}
