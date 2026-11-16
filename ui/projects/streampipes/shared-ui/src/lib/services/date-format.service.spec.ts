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
import { TestBed } from '@angular/core/testing';
import { TranslateService } from '@ngx-translate/core';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { DateFormatService } from './date-format.service';

describe('DateFormatService', () => {
    afterEach(() => vi.restoreAllMocks());

    it('reuses formatters while respecting language changes and missing dates', () => {
        const currentLang = vi.fn().mockReturnValue('en');
        TestBed.configureTestingModule({
            providers: [
                { provide: TranslateService, useValue: { currentLang } },
            ],
        });
        const service = TestBed.inject(DateFormatService);
        const timestamp = 1700000000000;
        const options: Intl.DateTimeFormatOptions = {
            dateStyle: 'medium',
            timeStyle: 'short',
        };
        const en = new Intl.DateTimeFormat('en', options).format(
            new Date(timestamp),
        );
        const de = new Intl.DateTimeFormat('de', options).format(
            new Date(timestamp),
        );
        const originalFormatter = Intl.DateTimeFormat;
        const constructor = vi
            .spyOn(Intl, 'DateTimeFormat')
            .mockImplementation(function (locales, formatOptions) {
                return new originalFormatter(locales, formatOptions);
            });
        expect(service.formatDate()).toBe('–');
        expect(constructor).not.toHaveBeenCalled();
        expect(service.formatDate(timestamp)).toBe(en);
        expect(service.formatDate(timestamp)).toBe(en);
        expect(constructor).toHaveBeenCalledTimes(1);
        currentLang.mockReturnValue('de');
        expect(service.formatDate(timestamp)).toBe(de);
        currentLang.mockReturnValue('en');
        expect(service.formatDate(timestamp)).toBe(en);
        expect(constructor).toHaveBeenCalledTimes(2);
    });
});
