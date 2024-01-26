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
import {
    EventPropertyUnion,
    FieldConfig,
    SourceConfig,
} from '@streampipes/platform-services';
import { DataExplorerFieldProviderService } from '../../../../services/data-explorer-field-provider-service';
import { WidgetConfigurationService } from '../../../../services/widget-configuration.service';

@Component({
    selector: 'sp-field-selection-panel',
    templateUrl: './field-selection-panel.component.html',
    styleUrls: ['./field-selection-panel.component.scss'],
})
export class FieldSelectionPanelComponent implements OnInit {
    MAX_INITIAL_FIELDS = 3;

    @Input() sourceConfig: SourceConfig;
    @Input() widgetId: string;

    expandFields = false;

    constructor(
        private fieldProvider: DataExplorerFieldProviderService,
        private widgetConfigService: WidgetConfigurationService,
    ) {}

    ngOnInit() {
        this.applyDefaultFields();
    }

    applyDefaultFields() {
        if (this.sourceConfig.queryConfig.fields.length === 0) {
            this.addAllFields();
            this.selectInitialFields();
        } else {
            const oldFields = this.sourceConfig.queryConfig.fields;
            this.sourceConfig.queryConfig.fields = [];
            this.addAllFields(oldFields);
        }
    }

    addAllFields(checkFields: FieldConfig[] = []) {
        this.sourceConfig.measure.eventSchema.eventProperties
            .sort((a, b) => a.runtimeName.localeCompare(b.runtimeName))
            .forEach(property => {
                // this ensures that dimension properties are not aggregated, this is not possible with the influxdb, See [STREAMPIPES-524]
                if (
                    this.sourceConfig.queryType === 'raw' ||
                    property.propertyScope !== 'DIMENSION_PROPERTY'
                ) {
                    const fieldConfig = checkFields.find(
                        field => field.runtimeName === property.runtimeName,
                    );
                    const isSelected = fieldConfig && fieldConfig.selected;
                    this.addField(property, isSelected, fieldConfig);
                }
            });
    }

    selectInitialFields() {
        this.sourceConfig.queryConfig.fields.forEach((field, index) => {
            if (index < this.MAX_INITIAL_FIELDS) {
                field.selected = true;
            }
        });
    }

    selectAllFields() {
        this.selectFields(true);
    }

    deselectAllFields() {
        this.selectFields(false);
    }

    selectFields(selected: boolean) {
        this.sourceConfig.queryConfig.fields.forEach(
            field => (field.selected = selected),
        );
        this.widgetConfigService.notify({
            widgetId: this.widgetId,
            refreshData: true,
            refreshView: true,
        });
    }

    addField(
        property: EventPropertyUnion,
        isSelected = false,
        fieldConfig: FieldConfig,
    ) {
        const selection: FieldConfig = {
            runtimeName: property.runtimeName,
            selected: isSelected,
            numeric: this.fieldProvider.isNumber(property),
        };
        selection.aggregations =
            fieldConfig && fieldConfig.aggregations
                ? fieldConfig.aggregations
                : [this.findDefaultAggregation(property)];
        this.sourceConfig.queryConfig.fields.push(selection);
    }

    findDefaultAggregation(property: EventPropertyUnion): string {
        if (this.fieldProvider.isNumber(property)) {
            return 'MEAN';
        } else if (
            this.fieldProvider.isBoolean(property) ||
            this.fieldProvider.isDimensionProperty(property) ||
            this.fieldProvider.isString(property)
        ) {
            return 'MODE';
        }
    }

    toggleExpandFields() {
        this.expandFields = !this.expandFields;
    }
}
