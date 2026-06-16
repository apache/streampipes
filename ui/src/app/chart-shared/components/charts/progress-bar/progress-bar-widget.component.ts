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

import { NgStyle } from '@angular/common';
import { Component, LOCALE_ID, OnInit, inject } from '@angular/core';
import {
    DataExplorerField,
    SpQueryResult,
} from '@streampipes/platform-services';
import { FlexDirective, LayoutDirective } from '@ngbracket/ngx-layout/flex';
import { BaseDataExplorerWidgetDirective } from '../base/base-data-explorer-widget.directive';
import { NoDataInDateRangeComponent } from '../base/no-data/no-data-in-date-range.component';
import { TooMuchDataComponent } from '../base/too-much-data/too-much-data.component';
import { SpInvalidConfigurationComponent } from '../base/invalid-configuration/invalid-configuration.component';
import { ChartRegistry } from '../../../registry/chart-registry.service';
import {
    ProgressBarAppearanceConfig,
    ProgressBarTargetSource,
    ProgressBarWidgetModel,
} from './model/progress-bar-widget.model';
import {
    clampValue,
    formatWidgetNumber,
    scaleResponsiveValue,
} from '../../../services/widget-render.utils';

interface ProgressBarViewModel {
    ratio: number;
    percentLabel: string;
    valueLabel: string;
    statusLabel: string;
}

@Component({
    selector: 'sp-data-explorer-progress-bar-widget',
    templateUrl: './progress-bar-widget.component.html',
    styleUrls: ['./progress-bar-widget.component.scss'],
    imports: [
        LayoutDirective,
        FlexDirective,
        NgStyle,
        NoDataInDateRangeComponent,
        TooMuchDataComponent,
        SpInvalidConfigurationComponent,
    ],
})
export class ProgressBarWidgetComponent
    extends BaseDataExplorerWidgetDirective<ProgressBarWidgetModel>
    implements OnInit
{
    widgetTypeLabel: string;
    progressView?: ProgressBarViewModel;

    private hasReceivedData = false;
    private latestData: SpQueryResult[] = [];
    private readonly locale = inject(LOCALE_ID);
    private readonly chartRegistry = inject(ChartRegistry);

    ngOnInit(): void {
        super.ngOnInit();
        this.widgetTypeLabel = this.chartRegistry.getChartTemplate(
            this.dataExplorerWidget.widgetType,
        ).label;
    }

    get appearanceConfig(): ProgressBarAppearanceConfig {
        this.dataExplorerWidget.baseAppearanceConfig ??= {
            backgroundColor: 'var(--color-bg-0)',
            textColor: 'var(--color-default-text)',
            widgetTitle: '',
            numberFormat: {},
            progressColor: '#2563EB',
            trackColor: 'rgba(148, 163, 184, 0.24)',
        };

        return this.dataExplorerWidget
            .baseAppearanceConfig as ProgressBarAppearanceConfig;
    }

    get widgetStyles(): Record<string, string> {
        const compactMode =
            (this.currentWidth ?? 0) < 520 || (this.currentHeight ?? 0) < 220;
        const fontScale = this.getFontScale();

        return {
            'background': this.appearanceConfig.backgroundColor,
            'color': this.appearanceConfig.textColor,
            '--progress-widget-padding': compactMode
                ? '0.75rem 0.85rem'
                : '1rem 1.1rem',
            '--progress-shell-gap': `${scaleResponsiveValue(
                8,
                16,
                fontScale,
                0.55,
                1.35,
            )}px`,
            '--progress-title-size': `${scaleResponsiveValue(
                16,
                20,
                fontScale,
                0.55,
                1.35,
            )}px`,
            '--progress-description-size': `${scaleResponsiveValue(
                12,
                14,
                fontScale,
                0.55,
                1.35,
            )}px`,
            '--progress-status-size': `${scaleResponsiveValue(
                10,
                13,
                fontScale,
                0.55,
                1.35,
            )}px`,
            '--progress-percent-size': `${scaleResponsiveValue(
                12,
                15,
                fontScale,
                0.55,
                1.35,
            )}px`,
            '--progress-track-height': `${scaleResponsiveValue(
                10,
                16,
                fontScale,
                0.55,
                1.35,
            )}px`,
            '--progress-primary-label-size': `${scaleResponsiveValue(
                18,
                38,
                fontScale,
                0.55,
                1.35,
            )}px`,
            '--progress-secondary-label-size': `${scaleResponsiveValue(
                12,
                15,
                fontScale,
                0.55,
                1.35,
            )}px`,
            '--progress-bar-fill-color':
                this.appearanceConfig.progressColor ?? '#2563EB',
            '--progress-bar-track-color':
                this.appearanceConfig.trackColor ?? 'rgba(148, 163, 184, 0.24)',
        };
    }

    get titleText(): string {
        return this.dataExplorerWidget.visualizationConfig.title?.trim() ?? '';
    }

    get descriptionText(): string {
        return (
            this.dataExplorerWidget.visualizationConfig.description?.trim() ??
            ''
        );
    }

    get showLabel(): boolean {
        return !!this.dataExplorerWidget.visualizationConfig.showLabel;
    }

    get progressWidth(): string {
        return `${clampValue((this.progressView?.ratio ?? 0) * 100, 0, 100)}%`;
    }

    beforeDataFetched(): void {
        if (this.progressView) {
            this.setShownComponents(false, true, false, false);
            return;
        }

        this.setShownComponents(false, false, true, false);
    }

    onDataReceived(spQueryResults: SpQueryResult[]): void {
        this.hasReceivedData = true;
        this.latestData = spQueryResults;
        this.refreshView();
    }

    onResize(_width: number, _height: number): void {
        this.refreshView();
    }

    refreshView(): void {
        this.updateProgressView();
    }

    handleUpdatedFields(
        addedFields: DataExplorerField[],
        removedFields: DataExplorerField[],
    ): void {
        const fieldUpdateInfo = {
            addedFields,
            removedFields,
            fieldProvider: this.fieldProvider,
        };

        this.dataExplorerWidget.visualizationConfig.currentValueField =
            this.fieldUpdateService.updateNumericField(
                this.dataExplorerWidget.visualizationConfig.currentValueField,
                fieldUpdateInfo,
            );
        this.dataExplorerWidget.visualizationConfig.targetField =
            this.fieldUpdateService.updateNumericField(
                this.dataExplorerWidget.visualizationConfig.targetField,
                fieldUpdateInfo,
            );
        this.refreshView();
    }

    private updateProgressView(): void {
        if (!this.isConfigurationValid()) {
            this.showInvalidConfiguration = true;
            this.progressView = undefined;
            this.setShownComponents(false, false, false, false);
            return;
        }

        this.showInvalidConfiguration = false;
        if (!this.hasReceivedData && this.latestData.length === 0) {
            return;
        }

        const firstQueryResult = this.latestData[0];
        const firstRow = this.getFirstRow(firstQueryResult);

        if (!firstQueryResult?.headers?.length || !firstRow) {
            this.progressView = undefined;
            this.setShownComponents(true, false, false, false);
            return;
        }

        const currentValue = this.getFieldValue(
            firstQueryResult,
            firstRow,
            this.dataExplorerWidget.visualizationConfig.currentValueField,
        );
        const targetValue = this.getTargetValue(firstQueryResult, firstRow);

        if (
            currentValue === undefined ||
            targetValue === undefined ||
            !Number.isFinite(currentValue) ||
            !Number.isFinite(targetValue) ||
            targetValue <= 0
        ) {
            this.showInvalidConfiguration = true;
            this.progressView = undefined;
            this.setShownComponents(false, false, false, false);
            return;
        }

        const rawRatio = currentValue / targetValue;
        const inverted =
            this.dataExplorerWidget.visualizationConfig.invertProgress;
        const unclampedRatio = inverted ? 1 - rawRatio : rawRatio;
        const ratio = this.dataExplorerWidget.visualizationConfig.clampProgress
            ? clampValue(unclampedRatio, 0, 1)
            : unclampedRatio;

        this.progressView = {
            ratio,
            percentLabel: `${formatWidgetNumber(
                ratio * 100,
                this.locale,
                this.appearanceConfig.numberFormat?.decimals,
            )}%`,
            valueLabel: `${formatWidgetNumber(
                currentValue,
                this.locale,
                this.appearanceConfig.numberFormat?.decimals,
            )} / ${formatWidgetNumber(
                targetValue,
                this.locale,
                this.appearanceConfig.numberFormat?.decimals,
            )}`,
            statusLabel: inverted ? 'Remaining' : 'Completed',
        };

        this.setShownComponents(false, true, false, false);
    }

    private isConfigurationValid(): boolean {
        const config = this.dataExplorerWidget.visualizationConfig;

        if (!config.currentValueField) {
            return false;
        }

        if (config.targetSource === 'field') {
            return !!config.targetField;
        }

        return config.targetValue !== undefined && config.targetValue !== null;
    }

    private getTargetValue(
        queryResult: SpQueryResult,
        row: unknown[],
    ): number | undefined {
        const targetSource = this.dataExplorerWidget.visualizationConfig
            .targetSource as ProgressBarTargetSource;

        if (targetSource === 'field') {
            return this.getFieldValue(
                queryResult,
                row,
                this.dataExplorerWidget.visualizationConfig.targetField,
            );
        }

        return this.dataExplorerWidget.visualizationConfig.targetValue;
    }

    private getFieldValue(
        queryResult: SpQueryResult,
        row: unknown[],
        field?: DataExplorerField,
    ): number | undefined {
        if (!field) {
            return undefined;
        }

        const fieldIndex = queryResult.headers.findIndex(
            header => header === field.fullDbName,
        );
        if (fieldIndex < 0) {
            return undefined;
        }

        const value = Number(row[fieldIndex]);
        return Number.isFinite(value) ? value : undefined;
    }

    private getFirstRow(queryResult?: SpQueryResult): unknown[] | undefined {
        return queryResult?.allDataSeries.find(series => series.rows.length > 0)
            ?.rows[0];
    }

    private getFontScale(): number {
        const width = this.currentWidth ?? 0;
        const height = this.currentHeight ?? 0;

        if (width <= 0 || height <= 0) {
            return 0.85;
        }

        const widthScale = clampValue(width / 420, 0.6, 1.35);
        const heightScale = clampValue(height / 260, 0.55, 1.35);

        return clampValue(Math.min(widthScale, heightScale), 0.55, 1.35);
    }
}
