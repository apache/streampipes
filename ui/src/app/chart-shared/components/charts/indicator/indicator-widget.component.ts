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
import { Component, inject, LOCALE_ID, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { FlexDirective, LayoutDirective } from '@ngbracket/ngx-layout/flex';
import {
    DataExplorerField,
    DataSeries,
    SpQueryResult,
} from '@streampipes/platform-services';
import { BaseDataExplorerWidgetDirective } from '../base/base-data-explorer-widget.directive';
import { SpInvalidConfigurationComponent } from '../base/invalid-configuration/invalid-configuration.component';
import { NoDataInDateRangeComponent } from '../base/no-data/no-data-in-date-range.component';
import { TooMuchDataComponent } from '../base/too-much-data/too-much-data.component';
import {
    IndicatorDeltaView,
    IndicatorGroupCardComponent,
    IndicatorGroupCardView,
} from './indicator-group-card.component';
import {
    IndicatorAppearanceConfig,
    IndicatorChartWidgetModel,
} from './model/indicator-chart-widget.model';

@Component({
    selector: 'sp-data-explorer-indicator-widget',
    templateUrl: './indicator-widget.component.html',
    styleUrls: ['./indicator-widget.component.scss'],
    imports: [
        LayoutDirective,
        FlexDirective,
        NgStyle,
        NoDataInDateRangeComponent,
        TooMuchDataComponent,
        SpInvalidConfigurationComponent,
        IndicatorGroupCardComponent,
    ],
})
export class IndicatorWidgetComponent
    extends BaseDataExplorerWidgetDirective<IndicatorChartWidgetModel>
    implements OnInit
{
    indicatorCards: IndicatorGroupCardView[] = [];
    widgetTypeLabel: string;

    private hasReceivedData = false;
    private latestData: SpQueryResult[] = [];
    private readonly locale = inject(LOCALE_ID);
    private readonly translateService = inject(TranslateService);

    ngOnInit(): void {
        super.ngOnInit();
        this.widgetTypeLabel = this.widgetRegistryService.getChartTemplate(
            this.dataExplorerWidget.widgetType,
        ).label;
    }

    get widgetStyles(): Record<string, string> {
        const compactMode =
            this.hasMultipleCards || (this.currentWidth ?? 0) < 640;
        const appearanceConfig = this.appearanceConfig;

        return {
            'background': appearanceConfig.backgroundColor,
            'color': appearanceConfig.textColor,
            '--indicator-selected-background': appearanceConfig.backgroundColor,
            '--indicator-padding': compactMode ? '12px' : '18px',
            '--indicator-gap': compactMode ? '10px' : '16px',
            '--indicator-title-size': compactMode ? '18px' : '24px',
            '--indicator-description-size': compactMode ? '12px' : '14px',
        };
    }

    get gridStyles(): Record<string, string> {
        return {
            '--indicator-grid-columns': `${this.gridColumnCount}`,
        };
    }

    get appearanceConfig(): IndicatorAppearanceConfig {
        this.dataExplorerWidget.baseAppearanceConfig ??= {
            backgroundColor: 'var(--color-bg-0)',
            textColor: 'var(--color-default-text)',
            widgetTitle: '',
        };

        return this.dataExplorerWidget
            .baseAppearanceConfig as IndicatorAppearanceConfig;
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

    get hasMultipleCards(): boolean {
        return this.indicatorCards.length > 1;
    }

    get cardWidth(): number {
        const width = this.currentWidth ?? 0;
        const gap = this.estimatedGap;
        return Math.max(
            (width - gap * Math.max(this.gridColumnCount - 1, 0)) /
                this.gridColumnCount,
            180,
        );
    }

    get cardHeight(): number {
        const availableHeight =
            (this.currentHeight ?? 0) -
            this.estimatedCopyHeight -
            this.estimatedPadding * 2 -
            this.estimatedGap * Math.max(this.gridRowCount - 1, 0);

        return Math.max(
            availableHeight / this.gridRowCount,
            this.hasMultipleCards ? 92 : 120,
        );
    }

    beforeDataFetched(): void {
        this.setShownComponents(false, false, true, false);
    }

    onDataReceived(spQueryResults: SpQueryResult[]): void {
        this.hasReceivedData = true;
        this.latestData = spQueryResults;
        this.updateIndicator();
    }

    onResize(_width: number, _height: number): void {
        this.refreshView();
    }

    refreshView(): void {
        this.updateIndicator();
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

        this.dataExplorerWidget.visualizationConfig.valueField =
            this.fieldUpdateService.updateSingleField(
                this.dataExplorerWidget.visualizationConfig.valueField,
                fieldUpdateInfo.fieldProvider.allFields,
                fieldUpdateInfo,
                () => true,
            );
        this.dataExplorerWidget.visualizationConfig.deltaField =
            this.fieldUpdateService.updateSingleField(
                this.dataExplorerWidget.visualizationConfig.deltaField,
                fieldUpdateInfo.fieldProvider.allFields,
                fieldUpdateInfo,
                () => true,
            );
        this.refreshView();
    }

    trackCard(_index: number, card: IndicatorGroupCardView): string {
        return card.id;
    }

    private updateIndicator(): void {
        if (
            this.dataExplorerWidget.visualizationConfig.configurationValid ===
            false
        ) {
            this.showInvalidConfiguration = true;
            this.indicatorCards = [];
            this.setShownComponents(false, false, false, false);
            return;
        }

        this.showInvalidConfiguration = false;
        if (!this.hasReceivedData && this.latestData.length === 0) {
            return;
        }

        const valueField =
            this.dataExplorerWidget.visualizationConfig.valueField;
        this.indicatorCards = this.buildCards(valueField);

        if (this.indicatorCards.length === 0) {
            this.setShownComponents(true, false, false, false);
            return;
        }

        this.setShownComponents(false, true, false, false);
    }

    private buildCards(
        valueField: DataExplorerField | undefined,
    ): IndicatorGroupCardView[] {
        if (!valueField) {
            return [];
        }

        const result = this.findQueryResult(valueField.sourceIndex);
        if (!result) {
            return [];
        }

        const seriesList = result?.allDataSeries ?? [];

        return seriesList
            .map((series, index) =>
                this.createCard(valueField, result, series, index),
            )
            .filter(
                (card): card is IndicatorGroupCardView => card !== undefined,
            );
    }

    private createCard(
        valueField: DataExplorerField,
        result: SpQueryResult,
        series: DataSeries,
        index: number,
    ): IndicatorGroupCardView | undefined {
        const currentValue = this.getSeriesFieldValue(
            result,
            series,
            valueField,
            0,
        );

        if (currentValue === undefined) {
            return undefined;
        }

        const groupInfo = this.makeGroupInfo(series.tags ?? {});

        return {
            id: `${result.sourceIndex}-${index}-${this.makeTagSignature(
                series.tags ?? {},
            )}`,
            label: groupInfo.label,
            displayValue: this.formatValue(currentValue),
            deltaView: this.buildDelta(valueField, series, currentValue),
        };
    }

    private buildDelta(
        valueField: DataExplorerField,
        series: DataSeries,
        currentValue: unknown,
    ): IndicatorDeltaView | undefined {
        if (!this.dataExplorerWidget.visualizationConfig.showDelta) {
            return undefined;
        }

        const deltaField =
            this.dataExplorerWidget.visualizationConfig.deltaField;
        const referenceValue = deltaField
            ? this.getMatchingGroupValue(deltaField, series.tags ?? {})
            : this.getSeriesFieldValue(
                  this.findQueryResult(valueField.sourceIndex),
                  series,
                  valueField,
                  1,
              );

        if (referenceValue === undefined) {
            return undefined;
        }

        const referenceLabel = deltaField ? deltaField.runtimeName : undefined;

        if (
            typeof currentValue === 'number' &&
            typeof referenceValue === 'number'
        ) {
            const delta = this.normalizeNumericDelta(
                currentValue - referenceValue,
            );
            const percentDelta =
                referenceValue === 0
                    ? undefined
                    : this.normalizeNumericDelta(
                          delta / Math.abs(referenceValue),
                      );

            return {
                icon:
                    delta > 0
                        ? 'trending_up'
                        : delta < 0
                          ? 'trending_down'
                          : 'trending_flat',
                label: this.formatSignedNumber(delta),
                detail:
                    percentDelta !== undefined
                        ? this.formatSignedPercent(percentDelta)
                        : undefined,
                meta: referenceLabel,
                tone:
                    delta > 0 ? 'positive' : delta < 0 ? 'negative' : 'neutral',
            };
        }

        const changed = currentValue !== referenceValue;

        return {
            icon: changed ? 'compare_arrows' : 'horizontal_rule',
            label: this.translateService.instant(
                changed ? 'Changed' : 'No change',
            ),
            meta: referenceLabel,
            tone: 'neutral',
        };
    }

    private getMatchingGroupValue(
        field: DataExplorerField,
        tags: Record<string, string>,
    ): unknown | undefined {
        const result = this.findQueryResult(field.sourceIndex);
        if (!result) {
            return undefined;
        }

        const matchingSeries = this.findMatchingSeries(result, tags);
        return this.getSeriesFieldValue(result, matchingSeries, field, 0);
    }

    private findMatchingSeries(
        result: SpQueryResult,
        tags: Record<string, string>,
    ): DataSeries | undefined {
        const targetSignature = this.makeTagSignature(tags);
        return (
            result.allDataSeries.find(
                series =>
                    this.makeTagSignature(series.tags ?? {}) ===
                    targetSignature,
            ) ??
            (result.allDataSeries.length === 1
                ? result.allDataSeries[0]
                : undefined)
        );
    }

    private findQueryResult(sourceIndex: number): SpQueryResult | undefined {
        return (
            this.latestData.find(item => item.sourceIndex === sourceIndex) ??
            this.latestData[sourceIndex]
        );
    }

    private getSeriesFieldValue(
        result: SpQueryResult | undefined,
        series: DataSeries | undefined,
        field: DataExplorerField,
        rowIndex: number,
    ): unknown | undefined {
        const row = series?.rows?.[rowIndex];
        if (!row) {
            return undefined;
        }

        const fieldIndex = this.findFieldIndex(
            result?.headers ?? series.headers,
            field,
        );

        return fieldIndex >= 0 ? row[fieldIndex] : undefined;
    }

    private findFieldIndex(
        headers: string[] | undefined,
        field: DataExplorerField,
    ): number {
        if (!headers) {
            return -1;
        }

        return headers.findIndex(
            header =>
                header === field.fullDbName || header === field.runtimeName,
        );
    }

    private makeGroupInfo(tags: Record<string, string>): {
        label?: string;
    } {
        const entries = Object.entries(tags);

        if (entries.length === 0) {
            return {};
        }

        if (entries.length === 1) {
            const [, value] = entries[0];
            return {
                label: value,
            };
        }

        return {
            label: entries
                .map(([key, value]) => `${key}: ${value}`)
                .join(' · '),
        };
    }

    private makeTagSignature(tags: Record<string, string>): string {
        return JSON.stringify(
            Object.entries(tags).sort(([left], [right]) =>
                left.localeCompare(right),
            ),
        );
    }

    private formatValue(value: unknown): string {
        if (typeof value === 'number') {
            return new Intl.NumberFormat(this.locale, {
                maximumFractionDigits: 3,
            }).format(value);
        }

        if (typeof value === 'boolean') {
            return this.translateService.instant(value ? 'True' : 'False');
        }

        if (value === null || value === undefined || value === '') {
            return '—';
        }

        return `${value}`;
    }

    private formatSignedNumber(value: number): string {
        return new Intl.NumberFormat(this.locale, {
            maximumFractionDigits: 3,
            signDisplay: 'exceptZero',
        }).format(value);
    }

    private formatSignedPercent(value: number): string {
        return new Intl.NumberFormat(this.locale, {
            style: 'percent',
            maximumFractionDigits: 1,
            signDisplay: 'exceptZero',
        }).format(value);
    }

    private normalizeNumericDelta(value: number): number {
        return Math.abs(value) < 0.000_000_1 ? 0 : value;
    }

    private get gridColumnCount(): number {
        if (this.indicatorCards.length <= 1) {
            return 1;
        }

        return Math.min(
            this.indicatorCards.length,
            Math.max(1, Math.floor((this.currentWidth ?? 0) / 220)),
        );
    }

    private get gridRowCount(): number {
        return Math.max(
            1,
            Math.ceil(this.indicatorCards.length / this.gridColumnCount),
        );
    }

    private get estimatedPadding(): number {
        return this.hasMultipleCards || (this.currentWidth ?? 0) < 640
            ? 12
            : 18;
    }

    private get estimatedGap(): number {
        return this.hasMultipleCards || (this.currentWidth ?? 0) < 640
            ? 10
            : 16;
    }

    private get estimatedCopyHeight(): number {
        let height = 0;

        if (this.titleText) {
            height += this.hasMultipleCards ? 26 : 34;
        }

        if (this.descriptionText) {
            height += this.hasMultipleCards ? 20 : 28;
        }

        if (height > 0) {
            height += this.estimatedGap;
        }

        return height;
    }
}
