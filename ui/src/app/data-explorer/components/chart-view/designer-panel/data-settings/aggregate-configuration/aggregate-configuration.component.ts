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

import { Component, inject, Input } from '@angular/core';
import { QueryConfig } from '@streampipes/platform-services';
import { ChartConfigurationService } from '../../../../../../data-explorer-shared/services/chart-configuration.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'sp-aggregate-configuration',
    templateUrl: './aggregate-configuration.component.html',
    styleUrls: ['./aggregate-configuration.component.scss'],
})
export class AggregateConfigurationComponent {
    @Input() queryConfig: QueryConfig;
    @Input() widgetId: string;

    translateService: TranslateService = inject(TranslateService);

    availableAggregations = [
        { value: 'ms', label: this.translateService.instant('Millisecond') },
        { value: 's', label: this.translateService.instant('Second') },
        { value: 'm', label: this.translateService.instant('Minute') },
        { value: 'h', label: this.translateService.instant('Hour') },
        { value: 'd', label: this.translateService.instant('Day') },
        { value: 'w', label: this.translateService.instant('Week') },
    ];

    constructor(private widgetConfigService: ChartConfigurationService) {}

    triggerDataRefresh() {
        if (this.widgetId) {
            this.widgetConfigService.notify({
                refreshData: true,
                refreshView: true,
            });
        }
    }
}
