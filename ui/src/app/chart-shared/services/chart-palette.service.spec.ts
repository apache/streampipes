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

import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';
import {
    ChartPaletteService,
    DEFAULT_CHART_PALETTE,
} from './chart-palette.service';

describe('ChartPaletteService', () => {
    let service: ChartPaletteService;

    beforeEach(() => {
        TestBed.configureTestingModule({});
        service = TestBed.inject(ChartPaletteService);
    });

    afterEach(() => {
        document.documentElement.style.removeProperty('--color-chart-1');
    });

    it('falls back to the default palette without theme variables', () => {
        expect(service.color(0)).toBe(DEFAULT_CHART_PALETTE[0]);
        expect(service.colors()).toEqual([...DEFAULT_CHART_PALETTE]);
    });

    it('wraps around for indices beyond the palette size', () => {
        expect(service.color(DEFAULT_CHART_PALETTE.length)).toBe(
            DEFAULT_CHART_PALETTE[0],
        );
        expect(service.color(-1)).toBe(
            DEFAULT_CHART_PALETTE[DEFAULT_CHART_PALETTE.length - 1],
        );
    });

    it('prefers the theme variable when the document defines it', () => {
        document.documentElement.style.setProperty(
            '--color-chart-1',
            '#123456',
        );
        expect(service.color(0)).toBe('#123456');
        expect(service.color(1)).toBe(DEFAULT_CHART_PALETTE[1]);
    });
});
