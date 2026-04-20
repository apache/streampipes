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

import { Component, Input, OnDestroy, OnInit, inject } from '@angular/core';
import { ChartConfigurationService } from '../../../../../chart-shared/services/chart-configuration.service';
import { DataExplorerWidgetModel } from '@streampipes/platform-services';
import { ChartTypeService } from '../../../../../chart-shared/services/chart-type.service';
import { ChartRegistry } from '../../../../../chart-shared/registry/chart-registry.service';
import { Subscription } from 'rxjs';
import { FlexDirective, LayoutDirective } from '@ngbracket/ngx-layout/flex';
import {
    FormFieldComponent,
    SplitSectionComponent,
} from '@streampipes/shared-ui';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { FormsModule } from '@angular/forms';
import { ColorPickerDirective } from 'ngx-color-picker';
import { NgComponentOutlet } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-chart-appearance-settings',
    templateUrl: './chart-appearance-settings.component.html',
    styleUrls: ['./chart-appearance-settings.component.scss'],
    imports: [
        FlexDirective,
        LayoutDirective,
        SplitSectionComponent,
        FormFieldComponent,
        MatRadioGroup,
        FormsModule,
        MatRadioButton,
        ColorPickerDirective,
        NgComponentOutlet,
        TranslatePipe,
    ],
})
export class ChartAppearanceSettingsComponent implements OnInit, OnDestroy {
    private widgetTypeService = inject(ChartTypeService);
    private widgetRegistryService = inject(ChartRegistry);
    private widgetConfigurationService = inject(ChartConfigurationService);

    @Input() currentlyConfiguredWidget: DataExplorerWidgetModel;

    backgroundOption: 'default' | 'custom' = 'default';
    textOption: 'default' | 'custom' = 'default';

    defaultBackgroundColor = 'var(--color-bg-0)';
    defaultTextColor = 'var(--color-default-text)';

    presetColors: string[] = [
        '#39B54A',
        '#1B1464',
        '#f44336',
        '#4CAF50',
        '#FFEB3B',
        '#FFFFFF',
        '#000000',
    ];

    widgetTypeSubscription: Subscription;
    extendedAppearanceConfigComponent: any;

    ngOnInit(): void {
        this.findWidget(this.currentlyConfiguredWidget.widgetType);
        this.widgetTypeSubscription =
            this.widgetTypeService.chartTypeChangeSubject.subscribe(() => {
                this.findWidget(this.currentlyConfiguredWidget.widgetType);
            });
        if (
            !this.currentlyConfiguredWidget.baseAppearanceConfig.backgroundColor
        ) {
            this.applyDefaultBackground();
        }
        if (!this.currentlyConfiguredWidget.baseAppearanceConfig.textColor) {
            this.applyDefaultText();
        }
        this.backgroundOption =
            this.currentlyConfiguredWidget.baseAppearanceConfig
                ?.backgroundColor === this.defaultBackgroundColor
                ? 'default'
                : 'custom';
        this.textOption =
            this.currentlyConfiguredWidget.baseAppearanceConfig?.textColor ===
            this.defaultTextColor
                ? 'default'
                : 'custom';
    }

    findWidget(widgetType: string): void {
        const widget = this.widgetRegistryService.getChartTemplate(widgetType);
        if (widget) {
            this.extendedAppearanceConfigComponent =
                widget.widgetAppearanceConfigurationComponent;
        }
    }

    triggerViewUpdate() {
        this.widgetConfigurationService.notify({
            refreshView: true,
            refreshData: false,
        });
    }

    ngOnDestroy() {
        this.widgetTypeSubscription?.unsubscribe();
    }

    onBackgroundChange(option: string) {
        if (option === 'default') {
            this.applyDefaultBackground();
        }
    }

    onTextChange(option: string): void {
        if (option === 'default') {
            this.applyDefaultText();
        }
    }

    applyDefaultBackground(): void {
        this.currentlyConfiguredWidget.baseAppearanceConfig.backgroundColor =
            this.defaultBackgroundColor;
    }

    applyDefaultText(): void {
        this.currentlyConfiguredWidget.baseAppearanceConfig.textColor =
            this.defaultTextColor;
    }
}
