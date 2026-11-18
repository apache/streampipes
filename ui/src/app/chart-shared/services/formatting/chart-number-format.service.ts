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

import { inject, Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { DataExplorerField } from '@streampipes/platform-services';
import {
    WidgetNumberAppearanceConfig,
    WidgetNumberFormatConfig,
} from '../../models/dataview-dashboard.model';

/**
 * Resolved formatting rules for one number, derived from the chart's
 * appearance configuration and the field being rendered.
 */
export interface NumberFormatOptions {
    /** Fixed number of fraction digits; undefined means automatic formatting. */
    decimals?: number;
    /** Render the stored value unchanged, without any formatting. */
    raw?: boolean;
    /** The value is a count or an integer field: never show fraction digits. */
    integer?: boolean;
}

type FieldTypeInfo = Pick<
    DataExplorerField,
    'fieldCharacteristics' | 'aggregation'
>;

const MAX_DECIMALS = 10;
const AUTO_MAX_FRACTION_DIGITS = 2;
const AUTO_MAX_SIGNIFICANT_DIGITS = 3;
const AUTO_PERCENT_FRACTION_DIGITS = 1;

export function normalizeDecimals(decimals: unknown): number | undefined {
    if (decimals === null || decimals === undefined || decimals === '') {
        return undefined;
    }

    const parsedValue = Number(decimals);
    if (!Number.isFinite(parsedValue)) {
        return undefined;
    }

    return Math.min(MAX_DECIMALS, Math.max(0, Math.round(parsedValue)));
}

/**
 * Single place that turns numbers into display strings for charts and
 * widgets. The rules are the same for ECharts axes and tooltips, tables,
 * value cards and single-value widgets:
 *
 * - raw: the stored value, unchanged (what older releases rendered)
 * - explicit decimals: exactly that many fraction digits
 * - automatic (the default): locale-aware, at most two fraction digits, or
 *   three significant digits for values below one, and no fraction digits
 *   for integer fields and counts.
 */
@Injectable({ providedIn: 'root' })
export class ChartNumberFormatService {
    private translateService = inject(TranslateService);
    private formatters = new Map<string, Intl.NumberFormat>();

    /**
     * Derives the formatting rules from a widget's appearance configuration.
     * Configurations saved by older releases have no numberFormat block and
     * resolve to automatic formatting.
     */
    resolveOptions(
        appearanceConfig: WidgetNumberAppearanceConfig | undefined,
        field?: FieldTypeInfo,
    ): NumberFormatOptions {
        const numberFormat: WidgetNumberFormatConfig =
            appearanceConfig?.numberFormat ?? {};
        return {
            decimals: normalizeDecimals(numberFormat.decimals),
            raw: numberFormat.rawValues === true,
            integer: field ? this.isIntegerField(field) : false,
        };
    }

    /** Formats one value; values that are not numbers are returned as text. */
    format(value: unknown, options: NumberFormatOptions = {}): string {
        if (value === null || value === undefined || value === '') {
            return '';
        }
        const numericValue = typeof value === 'number' ? value : Number(value);
        if (!Number.isFinite(numericValue)) {
            return String(value);
        }
        if (options.raw) {
            return String(value);
        }
        return this.getFormatter(numericValue, options).format(numericValue);
    }

    /**
     * Formats a ratio (0..1) as a percentage. Without explicit decimals a
     * percentage carries one fraction digit, or none when it is a whole number.
     */
    formatPercent(ratio: number, options: NumberFormatOptions = {}): string {
        const percentValue = ratio * 100;
        const percentOptions: NumberFormatOptions = {
            raw: options.raw,
            decimals:
                options.decimals ??
                (Number.isInteger(percentValue)
                    ? 0
                    : AUTO_PERCENT_FRACTION_DIGITS),
        };
        return `${this.format(percentValue, percentOptions)}%`;
    }

    /** Integer fields and COUNT aggregations never carry fraction digits. */
    isIntegerField(field: FieldTypeInfo | undefined): boolean {
        return (
            field?.fieldCharacteristics?.integer === true ||
            field?.aggregation === 'COUNT'
        );
    }

    private getFormatter(
        value: number,
        options: NumberFormatOptions,
    ): Intl.NumberFormat {
        const locale = this.currentLocale;
        const intlOptions = this.toIntlOptions(value, options);
        const key = `${locale}|${JSON.stringify(intlOptions)}`;
        let formatter = this.formatters.get(key);
        if (!formatter) {
            formatter = new Intl.NumberFormat(locale, intlOptions);
            this.formatters.set(key, formatter);
        }
        return formatter;
    }

    private toIntlOptions(
        value: number,
        options: NumberFormatOptions,
    ): Intl.NumberFormatOptions {
        if (options.integer) {
            return { maximumFractionDigits: 0 };
        }
        if (options.decimals !== undefined) {
            return {
                minimumFractionDigits: options.decimals,
                maximumFractionDigits: options.decimals,
            };
        }
        if (value !== 0 && Math.abs(value) < 1) {
            return { maximumSignificantDigits: AUTO_MAX_SIGNIFICANT_DIGITS };
        }
        return { maximumFractionDigits: AUTO_MAX_FRACTION_DIGITS };
    }

    private get currentLocale(): string {
        return (
            this.translateService.getCurrentLang() ||
            this.translateService.getFallbackLang() ||
            'en'
        );
    }
}
