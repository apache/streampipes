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

import { Component, computed, input } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import {
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-schema-preview-status',
    imports: [MatIcon, LayoutDirective, LayoutAlignDirective, TranslatePipe],
    styleUrl: './schema-preview-status.component.scss',
    host: { '[class.inline-status]': 'inline()' },
    template: `
        <div
            class="schema-status text-sm"
            [class]="
                'status-' +
                status() +
                (inline() ? '' : ' status-' + status() + '-bg')
            "
            fxLayout="row"
            fxLayoutAlign="start center"
            aria-live="polite"
        >
            <mat-icon aria-hidden="true">{{ icons[status()] }}</mat-icon>
            <span>
                @switch (status()) {
                    @case ('neutral') {
                        {{ 'Transformation disabled' | translate }}
                    }
                    @case ('info') {
                        {{ 'Running script' | translate }}
                    }
                    @case ('error') {
                        {{
                            (inline()
                                ? 'Script failed. Check the preview for details.'
                                : 'Script failed. Review the error and run it again.'
                            ) | translate
                        }}
                    }
                    @case ('warning') {
                        {{
                            (inline()
                                ? 'Run the script to update the preview.'
                                : 'Preview is out of date. Run the script to update it.'
                            ) | translate
                        }}
                    }
                    @case ('success') {
                        {{ 'Preview is up to date' | translate }}
                    }
                }
            </span>
        </div>
    `,
})
export class SchemaPreviewStatusComponent {
    active = input(true);
    running = input(false);
    error = input(false);
    outdated = input(false);
    inline = input(false);

    readonly status = computed(() => {
        if (!this.active()) return 'neutral';
        if (this.running()) return 'info';
        if (this.error()) return 'error';
        return this.outdated() ? 'warning' : 'success';
    });

    readonly icons = {
        neutral: 'pause_circle_outline',
        info: 'hourglass_top',
        error: 'error_outline',
        warning: 'warning_amber',
        success: 'check_circle',
    };
}
