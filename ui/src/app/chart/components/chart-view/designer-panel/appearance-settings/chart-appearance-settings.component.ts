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
    OnChanges,
    inject,
    ChangeDetectionStrategy,
} from '@angular/core';
import { ChartConfigurationService } from '../../../../../chart-shared/services/chart-configuration.service';
import { DataExplorerWidgetModel } from '@streampipes/platform-services';
import { FormFieldComponent } from '@streampipes/shared-ui';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { FormsModule } from '@angular/forms';
import { ColorPickerDirective } from 'ngx-color-picker';
import { TranslatePipe } from '@ngx-translate/core';
import { ChartSettingsSectionComponent } from '../../../../../chart-shared/components/chart-config/settings-section.component';

@Component({
    selector: 'sp-chart-appearance-settings',
    host: { class: 'flex-col' },
    templateUrl: './chart-appearance-settings.component.html',
    styleUrls: ['./chart-appearance-settings.component.scss'],
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [
        ChartSettingsSectionComponent,
        FormFieldComponent,
        MatRadioGroup,
        FormsModule,
        MatRadioButton,
        ColorPickerDirective,
        TranslatePipe,
    ],
})
export class ChartAppearanceSettingsComponent implements OnChanges {
    private widgetConfigurationService = inject(ChartConfigurationService);

    @Input() appearanceConfig: DataExplorerWidgetModel['baseAppearanceConfig'];

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

    ngOnChanges(): void {
        if (!this.appearanceConfig.backgroundColor) {
            this.applyDefaultBackground();
        }
        if (!this.appearanceConfig.textColor) {
            this.applyDefaultText();
        }
        this.backgroundOption =
            this.appearanceConfig?.backgroundColor ===
            this.defaultBackgroundColor
                ? 'default'
                : 'custom';
        this.textOption =
            this.appearanceConfig?.textColor === this.defaultTextColor
                ? 'default'
                : 'custom';
    }

    triggerViewUpdate() {
        this.widgetConfigurationService.notify({
            refreshView: true,
            refreshData: false,
        });
    }

    onBackgroundChange(option: string) {
        if (option === 'default') {
            this.applyDefaultBackground();
            this.triggerViewUpdate();
        }
    }

    onTextChange(option: string): void {
        if (option === 'default') {
            this.applyDefaultText();
            this.triggerViewUpdate();
        }
    }

    applyDefaultBackground(): void {
        this.appearanceConfig.backgroundColor = this.defaultBackgroundColor;
    }

    applyDefaultText(): void {
        this.appearanceConfig.textColor = this.defaultTextColor;
    }
}
