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
import { ChartConfigurationService } from '../../../../services/chart-configuration.service';
import { IndicatorAppearanceConfig } from '../model/indicator-chart-widget.model';
import {
    FormFieldComponent,
    SplitSectionComponent,
} from '@streampipes/shared-ui';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { FlexDirective } from '@ngbracket/ngx-layout/flex';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-indicator-appearance-config',
    templateUrl: './indicator-appearance-config.component.html',
    imports: [
        SplitSectionComponent,
        FormFieldComponent,
        MatFormField,
        MatInput,
        FormsModule,
        FlexDirective,
        TranslatePipe,
    ],
})
export class IndicatorAppearanceConfigComponent implements OnInit {
    @Input()
    appearanceConfig: IndicatorAppearanceConfig;

    constructor(
        private widgetConfigurationService: ChartConfigurationService,
    ) {}

    ngOnInit(): void {
        this.ensureAppearanceConfig();
    }

    get config(): IndicatorAppearanceConfig {
        return this.ensureAppearanceConfig();
    }

    updateFontSize(
        key: 'valueFontSize' | 'deltaFontSize',
        value: number | null,
    ): void {
        this.config[key] =
            value === null || value === undefined || value <= 0
                ? undefined
                : value;

        this.widgetConfigurationService.notify({
            refreshView: true,
            refreshData: false,
        });
    }

    private ensureAppearanceConfig(): IndicatorAppearanceConfig {
        this.appearanceConfig ??= {
            backgroundColor: 'var(--color-bg-0)',
            textColor: 'var(--color-default-text)',
            widgetTitle: '',
        };

        return this.appearanceConfig;
    }
}
