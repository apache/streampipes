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
import { WidgetNumberAppearanceConfig } from '../../../models/dataview-dashboard.model';
import { ChartConfigurationService } from '../../../services/chart-configuration.service';
import { FormFieldComponent } from '@streampipes/shared-ui';
import { FormsModule } from '@angular/forms';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-number-format-config',
    templateUrl: './number-format-config.component.html',
    imports: [
        FormFieldComponent,
        FormsModule,
        MatFormField,
        MatInput,
        TranslatePipe,
    ],
})
export class SpNumberFormatConfigComponent implements OnInit {
    private widgetConfigurationService = inject(ChartConfigurationService);

    @Input()
    appearanceConfig: WidgetNumberAppearanceConfig;

    ngOnInit(): void {
        this.appearanceConfig.numberFormat ??= {};
    }

    updateDecimals(decimals: number | string | null): void {
        this.appearanceConfig.numberFormat ??= {};
        this.appearanceConfig.numberFormat.decimals =
            this.normalizeDecimals(decimals);
        this.widgetConfigurationService.notify({
            refreshView: true,
            refreshData: false,
        });
    }

    get decimals(): number | null {
        return this.appearanceConfig.numberFormat?.decimals ?? null;
    }

    private normalizeDecimals(
        decimals: number | string | null,
    ): number | undefined {
        if (decimals === null || decimals === '') {
            return undefined;
        }

        const parsedDecimals = Number(decimals);
        if (!Number.isFinite(parsedDecimals)) {
            return undefined;
        }

        return Math.min(10, Math.max(0, Math.round(parsedDecimals)));
    }
}
