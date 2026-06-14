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

import { Component } from '@angular/core';
import { BaseWidgetConfig } from '../../base/base-widget-config';
import {
    ValueCardVisConfig,
    ValueCardWidgetModel,
} from '../model/value-card-widget.model';
import { DataExplorerField } from '@streampipes/platform-services';
import { SpVisualizationConfigOuterComponent } from '../../../chart-config/visualization-config-outer/visualization-config-outer.component';
import { SelectMultiplePropertiesConfigComponent } from '../../../chart-config/select-multiple-properties-config/select-multiple-properties-config.component';
import {
    FormFieldComponent,
    SplitSectionComponent,
} from '@streampipes/shared-ui';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { FlexDirective } from '@ngbracket/ngx-layout/flex';
import { TranslatePipe } from '@ngx-translate/core';
import { FieldOrderConfigComponent } from '../../../chart-config/field-order-config/field-order-config.component';
import { MatCheckbox } from '@angular/material/checkbox';

@Component({
    selector: 'sp-data-explorer-value-card-widget-config',
    templateUrl: './value-card-widget-config.component.html',
    imports: [
        SpVisualizationConfigOuterComponent,
        SplitSectionComponent,
        SelectMultiplePropertiesConfigComponent,
        FormFieldComponent,
        FieldOrderConfigComponent,
        MatFormField,
        MatInput,
        MatCheckbox,
        FlexDirective,
        FormsModule,
        TranslatePipe,
    ],
})
export class ValueCardWidgetConfigComponent extends BaseWidgetConfig<
    ValueCardWidgetModel,
    ValueCardVisConfig
> {
    updateSelectedFields(fields: DataExplorerField[]): void {
        this.currentlyConfiguredWidget.visualizationConfig.selectedFields =
            this.mergeSelectedFieldOrder(fields);
        this.triggerViewRefresh();
    }

    setSelectedFieldOrder(fields: DataExplorerField[]): void {
        this.currentlyConfiguredWidget.visualizationConfig.selectedFields =
            fields;
        this.triggerViewRefresh();
    }

    protected applyWidgetConfig(config: ValueCardVisConfig): void {
        config.selectedFields = this.getCompatibleSelectedFields(
            config.selectedFields,
        );
        config.title ??= '';
        config.description ??= '';
        config.showTimestamp ??= false;
    }

    protected requiredFieldsForChartPresent(): boolean {
        return this.fieldProvider.allFields.length > 0;
    }

    private getCompatibleSelectedFields(
        selectedFields?: DataExplorerField[],
    ): DataExplorerField[] {
        const availableFields = this.fieldProvider.allFields;

        if (!selectedFields?.length) {
            return availableFields.slice(
                0,
                Math.min(4, availableFields.length),
            );
        }

        return selectedFields.filter(selectedField =>
            availableFields.find(
                availableField =>
                    availableField.fullDbName === selectedField.fullDbName &&
                    availableField.sourceIndex === selectedField.sourceIndex,
            ),
        );
    }

    private mergeSelectedFieldOrder(
        nextSelectedFields: DataExplorerField[],
    ): DataExplorerField[] {
        const currentSelectedFields =
            this.currentlyConfiguredWidget.visualizationConfig.selectedFields ??
            [];

        const retainedFields = currentSelectedFields.filter(currentField =>
            nextSelectedFields.some(nextField =>
                this.isSameField(currentField, nextField),
            ),
        );

        const newlyAddedFields = nextSelectedFields.filter(
            nextField =>
                !currentSelectedFields.some(currentField =>
                    this.isSameField(currentField, nextField),
                ),
        );

        return [...retainedFields, ...newlyAddedFields];
    }

    private isSameField(a: DataExplorerField, b: DataExplorerField): boolean {
        return a.fullDbName === b.fullDbName && a.sourceIndex === b.sourceIndex;
    }
}
