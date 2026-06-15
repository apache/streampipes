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

import { Component, Input, OnInit, inject } from '@angular/core';
import { SplitSectionComponent } from '@streampipes/shared-ui';
import { TranslatePipe } from '@ngx-translate/core';
import { SpNumberFormatConfigComponent } from '../../../chart-config/number-format-config/number-format-config.component';
import { ValueCardAppearanceConfig } from '../model/value-card-widget.model';
import { FormFieldComponent } from '@streampipes/shared-ui';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { FlexDirective } from '@ngbracket/ngx-layout/flex';
import { ChartConfigurationService } from '../../../../services/chart-configuration.service';

@Component({
    selector: 'sp-value-card-widget-appearance-config',
    templateUrl: './value-card-appearance-config.component.html',
    imports: [
        SplitSectionComponent,
        TranslatePipe,
        SpNumberFormatConfigComponent,
        FormFieldComponent,
        MatFormField,
        MatInput,
        FormsModule,
        FlexDirective,
    ],
})
export class ValueCardWidgetAppearanceConfigComponent implements OnInit {
    private widgetConfigurationService = inject(ChartConfigurationService);

    @Input()
    appearanceConfig: ValueCardAppearanceConfig;

    ngOnInit(): void {
        this.ensureAppearanceConfig();
    }

    get config(): ValueCardAppearanceConfig {
        return this.ensureAppearanceConfig();
    }

    updateFontSize(
        key: 'labelFontSize' | 'valueFontSize',
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

    private ensureAppearanceConfig(): ValueCardAppearanceConfig {
        this.appearanceConfig ??= {
            backgroundColor: 'var(--color-bg-0)',
            textColor: 'var(--color-default-text)',
            widgetTitle: '',
            numberFormat: {},
        };

        return this.appearanceConfig;
    }
}
