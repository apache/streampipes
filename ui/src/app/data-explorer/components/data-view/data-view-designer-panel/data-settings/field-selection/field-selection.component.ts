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
import {
    EventPropertyUnion,
    FieldConfig,
    SourceConfig,
} from '@streampipes/platform-services';
import { WidgetConfigurationService } from '../../../../../services/widget-configuration.service';

@Component({
    selector: 'sp-field-selection',
    templateUrl: './field-selection.component.html',
    styleUrls: ['./field-selection.component.scss'],
})
export class FieldSelectionComponent implements OnInit {
    @Input() field: FieldConfig;
    @Input() sourceConfig: SourceConfig;

    @Output() addFieldEmitter: EventEmitter<EventPropertyUnion> =
        new EventEmitter<EventPropertyUnion>();

    constructor(private widgetConfigService: WidgetConfigurationService) {}

    ngOnInit() {
        if (!this.field.aggregations) {
            this.field.aggregations = [];
        }
    }

    triggerConfigurationUpdate() {
        this.widgetConfigService.notify({
            refreshData: true,
            refreshView: true,
        });
    }
}
