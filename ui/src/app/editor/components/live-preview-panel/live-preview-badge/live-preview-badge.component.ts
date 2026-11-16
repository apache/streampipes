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

import { Component, computed, inject, input } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { LivePreviewStore } from '../../../services/live-preview.store';

/**
 * Status of an element while the live preview runs. Shows no data values on purpose;
 * the values are in the preview panel.
 */
@Component({
    selector: 'sp-live-preview-badge',
    templateUrl: './live-preview-badge.component.html',
    styleUrls: ['./live-preview-badge.component.scss'],
    imports: [TranslatePipe],
})
export class LivePreviewBadgeComponent {
    private store = inject(LivePreviewStore, { optional: true });

    elementId = input.required<string>();
    elementName = input('');

    status = computed(() =>
        this.store?.active() ? this.store.status(this.elementId()) : undefined,
    );
    selected = computed(
        () => this.store?.selectedElementId() === this.elementId(),
    );

    select(event: Event): void {
        event.stopPropagation();
        this.store?.select(this.elementId());
    }
}
