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

import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class CollapseService {
    isCollapsed = signal<boolean>(this.loadCollapsed());

    toggleMenubar(): void {
        const next = !this.isCollapsed();
        this.isCollapsed.set(next);
        localStorage.setItem('sp-menubar-collapsed', JSON.stringify(next));
    }

    private loadCollapsed(): boolean {
        try {
            const v = localStorage.getItem('sp-menubar-collapsed');
            return v ? JSON.parse(v) : false;
        } catch {
            return false;
        }
    }
}
