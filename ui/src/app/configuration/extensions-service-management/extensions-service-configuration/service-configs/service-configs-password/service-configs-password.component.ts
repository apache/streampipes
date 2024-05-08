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

import { Component, Input } from '@angular/core';
import { ConfigurationService } from '../../../../shared/configuration.service';
import { ConfigItem } from '@streampipes/platform-services';

const hiddenPasswordString = '*****';

@Component({
    selector: 'sp-service-configs-password',
    templateUrl: './service-configs-password.component.html',
    styleUrls: ['./service-configs-password.component.scss'],
    providers: [ConfigurationService],
})
export class ServiceConfigsPasswordComponent {
    @Input() configuration: ConfigItem;

    password: string;
    show: boolean;
    className: string;
    private hide: boolean;

    constructor(public configService: ConfigurationService) {
        this.password = hiddenPasswordString;
        this.show = false;
        this.className = 'hideText';
        this.hide = true;
    }

    changePw() {
        if (this.password == hiddenPasswordString) {
            this.password = this.password.replace(hiddenPasswordString, '');
        }
        this.configuration.value = this.password;
        this.show = true;
    }

    addCssClass() {
        this.hide = !this.hide;

        this.className = !this.hide ? '' : 'hideText';
    }
}
