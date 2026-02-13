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

import { Component, inject, Input } from '@angular/core';
import { UserInfo } from '@streampipes/platform-services';
import { TranslateService } from '@ngx-translate/core';
import { SpBasicHeaderTitleComponent } from '@streampipes/shared-ui';

@Component({
    selector: 'sp-welcome',
    templateUrl: './welcome.component.html',
    styleUrls: ['./welcome.component.scss'],
    imports: [SpBasicHeaderTitleComponent],
})
export class WelcomeComponent {
    @Input()
    user: UserInfo;

    private translate = inject(TranslateService);

    get displayName(): string | null {
        return this.user?.displayName?.trim() || null;
    }

    get email(): string {
        return this.user?.username ?? '';
    }

    get greeting(): string {
        const hour = new Date().getHours();
        if (hour < 12) return this.translate.instant('Good morning');
        if (hour < 18) return this.translate.instant('Good afternoon');
        return this.translate.instant('Good evening');
    }
}
