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

@Injectable({ providedIn: 'root' })
export class LastUpdatedFormatterService {
    private translateService = inject(TranslateService);

    private exactTimeFormatter: Intl.DateTimeFormat;
    private relativeTimeFormatter: Intl.RelativeTimeFormat;
    private formatterLocale: string;

    public formatLastUpdatedAt(
        updatedAt: number | null | undefined,
        currentTime = Date.now(),
        emptyLabel = 'n/a',
    ): string {
        if (!updatedAt) {
            return emptyLabel;
        }

        const exactTime = this.formatExactTime(updatedAt);
        const relativeTime = this.formatRelativeTime(updatedAt, currentTime);
        return relativeTime ? `${relativeTime} (${exactTime})` : exactTime;
    }

    public formatExactLastUpdatedAt(
        updatedAt: number | null | undefined,
        emptyLabel = 'n/a',
    ): string {
        if (!updatedAt) {
            return emptyLabel;
        }

        return this.formatExactTime(updatedAt);
    }

    public formatRelativeLastUpdatedAt(
        updatedAt: number | null | undefined,
        currentTime = Date.now(),
    ): string | undefined {
        if (!updatedAt) {
            return undefined;
        }

        return this.formatRelativeTime(updatedAt, currentTime);
    }

    private formatExactTime(updatedAt: number): string {
        this.updateFormatters();
        return this.exactTimeFormatter.format(new Date(updatedAt));
    }

    private formatRelativeTime(
        updatedAt: number,
        currentTime: number,
    ): string | undefined {
        const ageInSeconds = Math.max(
            0,
            Math.round((currentTime - updatedAt) / 1000),
        );

        if (ageInSeconds < 60) {
            this.updateFormatters();
            return this.relativeTimeFormatter.format(-ageInSeconds, 'second');
        } else if (ageInSeconds < 3600) {
            this.updateFormatters();
            return this.relativeTimeFormatter.format(
                -Math.floor(ageInSeconds / 60),
                'minute',
            );
        } else if (ageInSeconds < 86400) {
            this.updateFormatters();
            return this.relativeTimeFormatter.format(
                -Math.floor(ageInSeconds / 3600),
                'hour',
            );
        }

        return undefined;
    }

    private updateFormatters(): void {
        const locale = this.currentLocale;
        if (this.formatterLocale === locale) {
            return;
        }

        this.exactTimeFormatter = new Intl.DateTimeFormat(locale, {
            dateStyle: 'medium',
            timeStyle: 'medium',
        });
        this.relativeTimeFormatter = new Intl.RelativeTimeFormat(locale, {
            numeric: 'auto',
        });
        this.formatterLocale = locale;
    }

    private get currentLocale(): string {
        return (
            this.translateService.getCurrentLang() ||
            this.translateService.getFallbackLang() ||
            'en'
        );
    }
}
