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

import { Component, computed, Input } from '@angular/core';

export type Mode = 'tree' | 'raw';

@Component({
    selector: 'sp-adapter-event-preview',
    standalone: false,
    templateUrl: './adapter-event-preview.component.html',
    styleUrl: './adapter-event-preview.component.scss',
})
export class AdapterEventPreviewComponent {
    @Input() value: unknown = null;

    /** Optional header title. */
    @Input() title = '';

    /** Initial view mode. */
    @Input() mode: Mode = 'tree';

    /** Label shown for the root node in tree mode. */
    @Input() rootLabel = 'root';

    /** Sort object keys in tree view. */
    @Input() sortKeys = true;

    /** Truncate very long strings in tree view (still copies full JSON). */
    @Input() maxStringLength = 220;

    @Input() dataCy = '';

    hasValue = computed(() => this.value !== null && this.value !== undefined);

    prettyJson = computed(() => {
        try {
            return JSON.stringify(this.value, null, 2);
        } catch {
            // Circular refs or non-serializable input
            return String(this.value);
        }
    });
}
