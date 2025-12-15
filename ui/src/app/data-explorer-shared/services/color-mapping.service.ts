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

import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root',
})
export class ColorMappingService {
    constructor() {}

    addMapping(
        colorMappings: { value: string; label: string; color: string }[],
    ): void {
        colorMappings.push({
            value: '',
            label: '',
            color: this.getDefaultColor(Math.random() * 1000),
        });
    }

    removeMapping(
        colorMappings: { value: string; label: string; color: string }[],
        index: number,
    ): { value: string; label: string; color: string }[] {
        return colorMappings.filter((_, i) => i !== index);
    }

    updateColor(
        currentMappings: { value: string; label: string; color: string }[],
        index: number,
        newColor: string,
    ): void {
        currentMappings[index].color = newColor;
    }

    getDefaultColor(value: string | number): string {
        let hash = 0x811c9dc5;
        const input = String(value);

        for (let i = 0; i < input.length; i++) {
            hash ^= input.charCodeAt(i);
            hash = (hash * 0x5bd1e995) & 0xffffffff;
            hash ^= hash >> 15;
        }

        const hue = Math.abs(hash) % 360;
        const saturation = 50 + (Math.abs(hash >> 8) % 20);
        const lightness = 45 + (Math.abs(hash >> 16) % 20);

        return `hsl(${hue}, ${saturation}%, ${lightness}%)`;
    }
}
