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
import { QueryConfig } from '@streampipes/platform-services';
import { ChartConfigurationService } from '../../../../../../chart-shared/services/chart-configuration.service';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { FormFieldComponent } from '@streampipes/shared-ui';
import { MatFormField } from '@angular/material/form-field';
import { MatOption, MatSelect } from '@angular/material/select';
import { MatInput } from '@angular/material/input';
import { FormsModule } from '@angular/forms';

type FillMode = 'none' | 'previous' | 'linear' | 'null' | 'number';

@Component({
    selector: 'sp-fill-configuration',
    templateUrl: './fill-configuration.component.html',
    styleUrls: ['./fill-configuration.component.scss'],
    imports: [
        LayoutDirective,
        LayoutAlignDirective,
        FlexDirective,
        FormFieldComponent,
        MatFormField,
        MatSelect,
        MatOption,
        MatInput,
        FormsModule,
        TranslatePipe,
    ],
})
export class FillConfigurationComponent implements OnInit {
    private widgetConfigService = inject(ChartConfigurationService);
    private translate = inject(TranslateService);

    @Input() queryConfig: QueryConfig;
    @Input() widgetId: string;

    fillMode: FillMode = 'none';
    customFillValue = 0;

    fillOptions: Array<{ value: FillMode; label: string }> = [
        { value: 'none', label: this.translate.instant('None') },
        { value: 'previous', label: this.translate.instant('Previous Value') },
        {
            value: 'linear',
            label: this.translate.instant('Linear Interpolation'),
        },
        { value: 'null', label: 'Null' },
        { value: 'number', label: this.translate.instant('Custom Value') },
    ];

    ngOnInit(): void {
        if (
            typeof this.queryConfig.fill === 'number' &&
            !Number.isNaN(this.queryConfig.fill)
        ) {
            this.fillMode = 'number';
            this.customFillValue = this.queryConfig.fill;
            return;
        }

        const configuredMode = this.queryConfig.fill;
        if (
            configuredMode === 'none' ||
            configuredMode === 'previous' ||
            configuredMode === 'linear' ||
            configuredMode === 'null'
        ) {
            this.fillMode = configuredMode;
            return;
        }

        this.queryConfig.fill = 'none';
    }

    updateFillMode(mode: FillMode): void {
        this.fillMode = mode;
        this.queryConfig.fill = mode === 'number' ? this.customFillValue : mode;
        this.triggerDataRefresh();
    }

    updateCustomFillValue(): void {
        this.queryConfig.fill = this.customFillValue;
        this.triggerDataRefresh();
    }

    triggerDataRefresh(): void {
        this.widgetConfigService.notify({
            refreshData: true,
            refreshView: true,
        });
    }
}
