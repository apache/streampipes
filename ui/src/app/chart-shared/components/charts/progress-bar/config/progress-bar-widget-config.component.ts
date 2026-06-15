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
    ProgressBarDisplayMode,
    ProgressBarTargetSource,
    ProgressBarVisConfig,
    ProgressBarWidgetModel,
} from '../model/progress-bar-widget.model';
import { DataExplorerField } from '@streampipes/platform-services';
import { SpVisualizationConfigOuterComponent } from '../../../chart-config/visualization-config-outer/visualization-config-outer.component';
import {
    FormFieldComponent,
    SplitSectionComponent,
} from '@streampipes/shared-ui';
import { SelectSinglePropertyConfigComponent } from '../../../chart-config/select-single-property-config/select-single-property-config.component';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { FlexDirective } from '@ngbracket/ngx-layout/flex';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-data-explorer-progress-bar-widget-config',
    templateUrl: './progress-bar-widget-config.component.html',
    imports: [
        SpVisualizationConfigOuterComponent,
        SplitSectionComponent,
        FormFieldComponent,
        SelectSinglePropertyConfigComponent,
        MatFormField,
        MatInput,
        MatCheckbox,
        MatRadioGroup,
        MatRadioButton,
        FlexDirective,
        FormsModule,
        TranslatePipe,
    ],
})
export class ProgressBarWidgetConfigComponent extends BaseWidgetConfig<
    ProgressBarWidgetModel,
    ProgressBarVisConfig
> {
    get availableNumericFields(): DataExplorerField[] {
        const primarySourceFields = this.fieldProvider.numericFields.filter(
            field => field.sourceIndex === 0,
        );
        return primarySourceFields.length > 0
            ? primarySourceFields
            : this.fieldProvider.numericFields;
    }

    setCurrentValueField(field: DataExplorerField): void {
        this.currentlyConfiguredWidget.visualizationConfig.currentValueField =
            field;
        this.triggerViewRefresh();
    }

    setTargetSource(targetSource: ProgressBarTargetSource): void {
        this.currentlyConfiguredWidget.visualizationConfig.targetSource =
            targetSource;

        if (
            targetSource === 'field' &&
            !this.currentlyConfiguredWidget.visualizationConfig.targetField
        ) {
            this.currentlyConfiguredWidget.visualizationConfig.targetField =
                this.availableNumericFields[0];
        }

        this.triggerViewRefresh();
    }

    setTargetField(field: DataExplorerField): void {
        this.currentlyConfiguredWidget.visualizationConfig.targetField = field;
        this.triggerViewRefresh();
    }

    setTargetValue(targetValue: string | number): void {
        const numericValue = Number(String(targetValue).replace(',', '.'));
        this.currentlyConfiguredWidget.visualizationConfig.targetValue =
            Number.isFinite(numericValue) ? numericValue : undefined;
        this.triggerViewRefresh();
    }

    setInvertProgress(invertProgress: boolean): void {
        this.currentlyConfiguredWidget.visualizationConfig.invertProgress =
            invertProgress;
        this.triggerViewRefresh();
    }

    setClampProgress(clampProgress: boolean): void {
        this.currentlyConfiguredWidget.visualizationConfig.clampProgress =
            clampProgress;
        this.triggerViewRefresh();
    }

    setDisplayMode(displayMode: ProgressBarDisplayMode): void {
        this.currentlyConfiguredWidget.visualizationConfig.displayMode =
            displayMode;
        this.triggerViewRefresh();
    }

    setShowLabel(showLabel: boolean): void {
        this.currentlyConfiguredWidget.visualizationConfig.showLabel =
            showLabel;
        this.triggerViewRefresh();
    }

    protected applyWidgetConfig(config: ProgressBarVisConfig): void {
        config.currentValueField = this.fieldService.getSelectedField(
            config.currentValueField,
            this.availableNumericFields,
            () => this.availableNumericFields[0],
        );
        config.targetSource ??= 'fixed';
        config.targetField = this.fieldService.getSelectedField(
            config.targetField,
            this.availableNumericFields,
            () => this.availableNumericFields[0],
        );
        config.targetValue ??= 100;
        config.invertProgress ??= false;
        config.clampProgress ??= true;
        config.displayMode ??= 'percent-and-value';
        config.showLabel ??= true;
        config.title ??= '';
        config.description ??= '';
    }

    protected requiredFieldsForChartPresent(): boolean {
        return this.availableNumericFields.length > 0;
    }
}
