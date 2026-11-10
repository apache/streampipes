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

import { DOCUMENT } from '@angular/common';
import { inject, Injectable } from '@angular/core';

/**
 * Resolves theme variables for canvas-rendered charts. ECharts draws on a
 * canvas and cannot consume CSS variables, so colours that must follow the
 * active theme (operator themes, dark mode) are read from the document root
 * at render time and fall back to the light-mode defaults otherwise.
 */
@Injectable({ providedIn: 'root' })
export class ChartThemeService {
    private document = inject(DOCUMENT, { optional: true });

    /** Value of the CSS variable `name` on the document root, or `fallback`. */
    variable(name: string, fallback: string): string {
        const root = this.document?.documentElement;
        if (!root || typeof getComputedStyle !== 'function') {
            return fallback;
        }
        const value = getComputedStyle(root).getPropertyValue(name).trim();
        return value.length > 0 ? value : fallback;
    }

    /** Like {@link variable}, but `undefined` when the theme does not define it. */
    optionalVariable(name: string): string | undefined {
        const value = this.variable(name, '');
        return value.length > 0 ? value : undefined;
    }
}
