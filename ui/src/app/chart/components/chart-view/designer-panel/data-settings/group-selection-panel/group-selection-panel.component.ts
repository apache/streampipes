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

import {
    Component,
    Input,
    OnInit,
    inject,
    ChangeDetectionStrategy,
} from '@angular/core';
import { ChartFieldProviderService } from '../../../../../../chart-shared/services/chart-field-provider.service';
import { ChartConfigurationService } from '../../../../../../chart-shared/services/chart-configuration.service';
import {
    EventPropertyUnion,
    FieldConfig,
    SourceConfig,
} from '@streampipes/platform-services';
import { MatButton } from '@angular/material/button';
import { MatCheckbox } from '@angular/material/checkbox';
import { FormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { ChartSettingsSectionComponent } from '../../../../../../chart-shared/components/chart-config/settings-section.component';

@Component({
    selector: 'sp-group-selection-panel',
    templateUrl: './group-selection-panel.component.html',
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [
        ChartSettingsSectionComponent,
        MatButton,
        MatCheckbox,
        FormsModule,
        TranslatePipe,
    ],
})
export class GroupSelectionPanelComponent implements OnInit {
    private fieldProvider = inject(ChartFieldProviderService);
    private widgetConfigService = inject(ChartConfigurationService);

    @Input() sourceConfig: SourceConfig;

    get selectedNames(): string[] {
        return (
            this.sourceConfig.queryConfig.groupBy
                ?.filter(field => field.selected)
                .map(field => field.runtimeName) ?? []
        );
    }

    ngOnInit() {
        const groupByFields = this.sourceConfig.queryConfig.groupBy;

        if (groupByFields === undefined || groupByFields.length === 0) {
            this.applyDefaultFields();
        }
    }

    applyDefaultFields() {
        const selected = new Set(
            this.sourceConfig.queryConfig.groupBy
                ?.filter(field => field.selected)
                .map(field => field.runtimeName),
        );
        this.sourceConfig.queryConfig.groupBy = [];
        this.addAllFields();
        this.sourceConfig.queryConfig.groupBy.forEach(
            field => (field.selected = selected.has(field.runtimeName)),
        );
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
            refreshData: true,
            refreshView: true,
        });
    }
}
