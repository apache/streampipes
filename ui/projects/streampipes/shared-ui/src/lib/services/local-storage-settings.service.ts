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

import { Injectable, effect, signal, Signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class LocalStorageService {
    private readonly prefix = 'sp-';

    private buildKey(key: string): string {
        return `${this.prefix}${key}`;
    }

    get<T>(key: string, fallback: T): T {
        try {
            const raw = localStorage.getItem(this.buildKey(key));
            if (raw === null) {
                return fallback;
            }
            return JSON.parse(raw) as T;
        } catch {
            return fallback;
        }
    }

    set<T>(key: string, value: T): void {
        try {
            localStorage.setItem(this.buildKey(key), JSON.stringify(value));
        } catch {
            // ignore quota / disabled storage errors
        }
    }

    /**
     * Creates a signal bound to a localStorage key.
     * Whenever the signal changes, the new value is persisted.
     */
    signalFor<T>(key: string, fallback: T): Signal<T> {
        const s = signal<T>(this.get<T>(key, fallback));

        // Persist whenever the signal changes
        effect(() => {
            const value = s();
            this.set<T>(key, value);
        });

        return s.asReadonly ? s.asReadonly() : (s as Signal<T>);
    }
}
