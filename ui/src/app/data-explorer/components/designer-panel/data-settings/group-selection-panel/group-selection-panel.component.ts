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
import { DataExplorerFieldProviderService } from '../../../../services/data-explorer-field-provider-service';
import { WidgetConfigurationService } from '../../../../services/widget-configuration.service';
import {
    EventPropertyUnion,
    FieldConfig,
    SourceConfig,
} from '@streampipes/platform-services';

@Component({
    selector: 'sp-group-selection-panel',
    templateUrl: './group-selection-panel.component.html',
    styleUrls: ['./group-selection-panel.component.scss'],
})
export class GroupSelectionPanelComponent implements OnInit {
    @Input() sourceConfig: SourceConfig;
    @Input() widgetId: string;

    constructor(
        private fieldProvider: DataExplorerFieldProviderService,
        private widgetConfigService: WidgetConfigurationService,
    ) {}

    ngOnInit() {
        const groupByFields = this.sourceConfig.queryConfig.groupBy;

        if (groupByFields === undefined || groupByFields.length === 0) {
            this.applyDefaultFields();
        }
    }

    applyDefaultFields() {
        this.sourceConfig.queryConfig.groupBy = [];
        this.addAllFields();
    }

    addAllFields() {
        this.sourceConfig.measure.eventSchema.eventProperties.forEach(
            property => {
                if (this.fieldProvider.isDimensionProperty(property)) {
                    this.addField(property);
                }
            },
        );
    }

    selectAllFields() {
        this.selectFields(true);
    }

    deselectAllFields() {
        this.selectFields(false);
    }

    selectFields(selected: boolean) {
        this.sourceConfig.queryConfig.groupBy.forEach(
            field => (field.selected = selected),
        );
        this.widgetConfigService.notify({
            widgetId: this.widgetId,
            refreshData: true,
            refreshView: true,
        });
    }

    addField(property: EventPropertyUnion) {
        const selection: FieldConfig = {
            runtimeName: property.runtimeName,
            selected: false,
            numeric: this.fieldProvider.isNumber(property),
        };
        this.sourceConfig.queryConfig.groupBy.push(selection);
    }

    triggerConfigurationUpdate() {
        this.widgetConfigService.notify({
            widgetId: this.widgetId,
            refreshData: true,
            refreshView: true,
        });
    }
}
