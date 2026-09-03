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

import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-page-header',
    templateUrl: './page-header.component.html',
    styleUrls: ['./page-header.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [MatIconButton, MatIcon, MatTooltip, TranslatePipe],
})
export class SpPageHeaderComponent {
    constructor(private router: Router) {}

    @Input({ required: true })
    title: string;

    @Input()
    description: string | undefined;

    @Input()
    icon: string | undefined;

    @Input()
    iconColor = 'var(--color-primary)';

    @Input()
    backLinkTarget: string[] | undefined;

    @Input()
    backLinkLabel = 'Back';

    navigateBack(): void {
        if (this.backLinkTarget?.length) {
            this.router.navigate(this.backLinkTarget);
        }
    }
}
