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

import {
    Component,
    HostBinding,
    Input,
    OnChanges,
    SimpleChanges,
} from '@angular/core';
import { SpColorizationService } from '../../services/colorization.service';

@Component({
    selector: 'sp-label',
    templateUrl: './sp-label.component.html',
    styleUrls: ['./sp-label.component.scss'],
    standalone: false,
})
export class SpLabelComponent implements OnChanges {
    @Input()
    labelText: string | number;

    @Input()
    small = false;

    @Input()
    icon: string | undefined = undefined;

    @Input()
    minWidth = undefined;

    @Input()
    size: 'small' | 'medium' | 'large' = 'medium';

    /** Visual style (chips/badges) */
    @Input() variant: 'soft' | 'solid' | 'outline' = 'soft';

    /** Shape controls radius + padding character */
    @Input() shape: 'pill' | 'rounded' | 'badge' = 'badge';

    /** Text transform */
    @Input() textCase: 'normal' | 'uppercase' | 'capitalize' = 'normal';

    /** Preferred way: semantic token */
    @Input() tone?: 'success' | 'warning' | 'error' | 'info' | 'neutral';

    /** Or provide any CSS color: hex/rgb/var(...) */
    @Input() color?: string;

    /** Optional override: which surface to mix into for soft variant */
    @Input() surface: string = 'var(--color-bg-2)';

    @HostBinding('attr.data-variant') hostVariant: string;
    @HostBinding('attr.data-size') hostSize: string;
    @HostBinding('attr.data-shape') hostShape: string;
    @HostBinding('attr.data-case') hostCase: string;

    @HostBinding('style.--sp-label-color') hostColor: string;
    @HostBinding('style.--sp-label-contrast') hostContrast: string;
    @HostBinding('style.--sp-label-fg') hostFgOverride: string | null = null;

    @HostBinding('style.--sp-label-min-width')
    get hostMinWidth() {
        return this.minWidth ?? null;
    }

    private toneMap: Record<NonNullable<SpLabelComponent['tone']>, string> = {
        success: 'var(--color-success)',
        warning: 'var(--color-warning)',
        error: 'var(--color-error)',
        info: 'var(--color-info)',
        neutral: 'var(--color-neutral)',
    };

    constructor(private colorizationService: SpColorizationService) {}

    ngOnChanges(changes: SimpleChanges) {
        this.hostVariant = this.variant;
        this.hostSize = this.size;
        this.hostShape = this.shape;
        this.hostCase = this.textCase;

        const base = this.getBaseColor();
        this.hostColor = base;

        // For solid backgrounds (and generally), ensure a readable contrast color.
        // If base is var(...), generateContrastColor may return fallback; CSS will still work.
        this.hostContrast =
            this.colorizationService.generateContrastColor(base);

        // Guardrail: if the "accent-as-text" is too light, force readable fg for soft/outline
        // (You can remove this block if you want pure CSS behavior.)
        if (this.variant !== 'solid') {
            this.hostFgOverride = this.colorizationService.ensureContrast(
                base,
                this.surface,
                4.5,
            );
        } else {
            this.hostFgOverride = null;
        }
    }

    private getBaseColor(): string {
        if (this.color) return this.color;
        if (this.tone) return this.toneMap[this.tone];
        return 'var(--sp-color-neutral)';
    }
}
