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
    ValueCardAppearanceConfig,
    ValueCardVisConfig,
    ValueCardWidgetModel,
} from './model/value-card-widget.model';
import { ResultLabelService } from '../../../services/result-label.service';

interface ValueCardView {
    id: string;
    label: string;
    value: string;
}

@Component({
    selector: 'sp-data-explorer-value-card-widget',
    templateUrl: './value-card-widget.component.html',
    styleUrls: ['./value-card-widget.component.scss'],
    imports: [
        LayoutDirective,
        FlexDirective,
        NgStyle,
        NoDataInDateRangeComponent,
        TooMuchDataComponent,
        SpInvalidConfigurationComponent,
    ],
})
export class ValueCardWidgetComponent
    extends BaseDataExplorerWidgetDirective<ValueCardWidgetModel>
    implements OnInit
{
    valueCards: ValueCardView[] = [];
    widgetTypeLabel: string;
    latestTimestampLabel?: string;

    private hasReceivedData = false;
    private latestData: SpQueryResult[] = [];
    private readonly locale = inject(LOCALE_ID);
    private readonly chartRegistry = inject(ChartRegistry);
    private readonly resultLabelService = inject(ResultLabelService);

    ngOnInit(): void {
        super.ngOnInit();
        this.widgetTypeLabel = this.chartRegistry.getChartTemplate(
            this.dataExplorerWidget.widgetType,
        ).label;
    }

    get appearanceConfig(): ValueCardAppearanceConfig {
        this.dataExplorerWidget.baseAppearanceConfig ??= {
            backgroundColor: 'var(--color-bg-0)',
            textColor: 'var(--color-default-text)',
            widgetTitle: '',
            numberFormat: {},
        };

        return this.dataExplorerWidget
            .baseAppearanceConfig as ValueCardAppearanceConfig;
    }

    get widgetStyles(): Record<string, string> {
        const compactMode =
            (this.currentWidth ?? 0) < 640 || this.hasManyValues;
        const fontScale = this.getFontScale();

        return {
            'background': this.appearanceConfig.backgroundColor,
            'color': this.appearanceConfig.textColor,
            '--value-card-padding': compactMode ? '14px' : '20px',
            '--value-card-gap': compactMode ? '12px' : '18px',
            '--value-card-title-size': compactMode ? '18px' : '24px',
            '--value-card-description-size': compactMode ? '12px' : '14px',
            '--value-card-row-padding': `${this.scaleValue(12, 22, fontScale)}px`,
            '--value-card-label-size': `${this.resolveFontSize(
                this.appearanceConfig.labelFontSize,
                11,
                16,
                fontScale,
            )}px`,
            '--value-card-value-size': `${this.resolveFontSize(
                this.appearanceConfig.valueFontSize,
                20,
                42,
                fontScale,
            )}px`,
            '--value-card-row-gap': `${this.scaleValue(4, 10, fontScale)}px`,
            '--value-card-border-color':
                'color-mix(in srgb, currentColor 10%, transparent)',
            '--value-card-divider-color':
                'color-mix(in srgb, currentColor 8%, transparent)',
            '--value-card-muted-color':
                'color-mix(in srgb, currentColor 68%, transparent)',
            '--value-card-panel-background':
                'color-mix(in srgb, currentColor 6%, transparent)',
        };
    }

    get content(): ValueCardVisConfig {
        return this.dataExplorerWidget.visualizationConfig;
    }

    get titleText(): string {
        return this.content.title?.trim() ?? '';
    }

    get descriptionText(): string {
        return this.content.description?.trim() ?? '';
    }

    get showTimestamp(): boolean {
        return !!this.content.showTimestamp;
    }

    get hasManyValues(): boolean {
        return this.valueCards.length > 3;
    }

    beforeDataFetched(): void {
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
        this.updateCards();
    }

    handleUpdatedFields(
        addedFields: DataExplorerField[],
        removedFields: DataExplorerField[],
    ): void {
        const currentSelection = this.content.selectedFields ?? [];
        this.content.selectedFields = currentSelection
            .filter(
                field =>
                    !removedFields.find(
                        removedField =>
                            removedField.fullDbName === field.fullDbName &&
                            removedField.sourceIndex === field.sourceIndex,
                    ),
            )
            .concat(
                addedFields.filter(
                    field =>
                        !currentSelection.find(
                            currentField =>
                                currentField.fullDbName === field.fullDbName &&
                                currentField.sourceIndex === field.sourceIndex,
                        ),
                ),
            );
        this.refreshView();
    }

    trackCard(_index: number, card: ValueCardView): string {
        return card.id;
    }

    private updateCards(): void {
        if (this.content.configurationValid === false) {
            this.showInvalidConfiguration = true;
            this.valueCards = [];
            this.setShownComponents(false, false, false, false);
            return;
        }

        this.showInvalidConfiguration = false;
        if (!this.hasReceivedData && this.latestData.length === 0) {
            return;
        }

        this.latestTimestampLabel = this.getLatestTimestampLabel();
        const selectedFields = this.content.selectedFields ?? [];
        this.valueCards = selectedFields
            .map(field => this.makeCard(field))
            .filter((card): card is ValueCardView => !!card);

        if (this.valueCards.length === 0) {
            this.setShownComponents(true, false, false, false);
            return;
        }

        this.setShownComponents(false, true, false, false);
    }

    private makeCard(field: DataExplorerField): ValueCardView | undefined {
        const sourceResult = this.latestData[field.sourceIndex];

        if (!sourceResult?.headers?.length) {
            return undefined;
        }

        const fieldIndex = sourceResult.headers.findIndex(
            header => header === field.fullDbName,
        );

        if (fieldIndex < 0) {
            return undefined;
        }

        const firstRow = this.getFirstRow(sourceResult);
        if (!firstRow) {
            return undefined;
        }

        const queryConfig =
            this.dataExplorerWidget.dataConfig.sourceConfigs[field.sourceIndex]
                ?.queryConfig;

        return {
            id: `${field.sourceIndex}-${field.fullDbName}`,
            label: queryConfig
                ? this.resultLabelService.resolveLabel(queryConfig, field)
                : (field.runtimeName ?? field.fullDbName),
            value: this.formatValue(firstRow[fieldIndex]),
        };
    }

    private getFirstRow(queryResult: SpQueryResult): unknown[] | undefined {
        return queryResult.allDataSeries.find(series => series.rows.length > 0)
            ?.rows[0];
    }

    private formatValue(value: unknown): string {
        if (value === null || value === undefined || value === '') {
            return '-';
        }

        if (typeof value === 'number' && Number.isFinite(value)) {
            const decimals = this.normalizeDecimals(
                this.appearanceConfig.numberFormat?.decimals,
            );

            return new Intl.NumberFormat(this.locale, {
                maximumFractionDigits: decimals ?? 20,
                minimumFractionDigits: decimals ?? 0,
            }).format(value);
        }

        if (typeof value === 'boolean') {
            return value ? 'True' : 'False';
        }

        return String(value);
    }

    private normalizeDecimals(decimals: unknown): number | undefined {
        if (decimals === null || decimals === undefined || decimals === '') {
            return undefined;
        }

        const parsedValue = Number(decimals);
        if (!Number.isFinite(parsedValue)) {
            return undefined;
        }

        return Math.min(10, Math.max(0, Math.round(parsedValue)));
    }

    private getLatestTimestampLabel(): string | undefined {
        const firstQueryResult = this.latestData.find(
            queryResult => !!this.getFirstRow(queryResult),
        );

        if (!firstQueryResult) {
            return undefined;
        }

        const firstTimestamp = this.getFirstTimestamp(firstQueryResult);
        if (firstTimestamp === undefined) {
            return undefined;
        }

        return new Intl.DateTimeFormat(this.locale, {
            dateStyle: 'medium',
            timeStyle: 'medium',
        }).format(new Date(firstTimestamp));
    }

    private getFirstTimestamp(queryResult: SpQueryResult): number | undefined {
        const timeIndex = queryResult.headers.findIndex(
            header => header === 'time',
        );

        if (timeIndex < 0) {
            return undefined;
        }

        const firstRow = this.getFirstRow(queryResult);
        if (!firstRow) {
            return undefined;
        }

        const firstTimestamp = new Date(
            firstRow[timeIndex] as string,
        ).getTime();
        return Number.isFinite(firstTimestamp) ? firstTimestamp : undefined;
    }

    private getFontScale(): number {
        const width = this.currentWidth ?? 0;
        const height = this.currentHeight ?? 0;
        const rowCount = Math.max(this.valueCards.length, 1);

        if (width <= 0 || height <= 0) {
            return 0.8;
        }

        const widthScale = this.clamp(width / 640, 0.7, 1.45);
        const availableHeight = Math.max(height - 110, 120);
        const heightPerRowScale = this.clamp(
            availableHeight / (rowCount * 84),
            0.7,
            1.45,
        );

        return this.clamp(Math.min(widthScale, heightPerRowScale), 0.7, 1.45);
    }

    private scaleValue(min: number, max: number, scale: number): number {
        const normalizedScale = (scale - 0.7) / 0.75;
        const scaledValue = min + (max - min) * normalizedScale;
        return Math.round(this.clamp(scaledValue, min, max));
    }

    private resolveFontSize(
        manualSize: number | undefined,
        min: number,
        max: number,
        scale: number,
    ): number {
        if (
            manualSize === undefined ||
            manualSize === null ||
            Number.isNaN(Number(manualSize)) ||
            manualSize <= 0
        ) {
            return this.scaleValue(min, max, scale);
        }

        return Math.round(Math.max(1, manualSize) * scale * 10) / 10;
    }

    private clamp(value: number, min: number, max: number): number {
        return Math.min(Math.max(value, min), max);
    }

    get valueListColumns(): number {
        return 1;
    }
}
