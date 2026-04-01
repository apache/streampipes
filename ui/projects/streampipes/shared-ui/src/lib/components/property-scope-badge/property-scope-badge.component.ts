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
    ChangeDetectionStrategy,
    Component,
    computed,
    inject,
    input,
} from '@angular/core';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'sp-property-scope-badge',
    templateUrl: './property-scope-badge.component.html',
    styleUrls: ['./property-scope-badge.component.scss'],
    imports: [MatTooltip],
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PropertyScopeBadgeComponent {
    private readonly translateService = inject(TranslateService);

    readonly propertyScope = input<string | undefined>(undefined);

    readonly badgeText = computed(() =>
        this.propertyScope() === 'DIMENSION_PROPERTY' ? 'D' : null,
    );

    readonly tooltipText = computed(() =>
        this.propertyScope() === 'DIMENSION_PROPERTY'
            ? this.translateService.instant('Dimension')
            : null,
    );
}
