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

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AlertType } from '@streampipes/platform-services';

@Component({
    selector: 'sp-alert-banner',
    templateUrl: './alert-banner.component.html',
    styleUrls: ['./alert-banner.component.scss'],
    standalone: false,
})
export class SpAlertBannerComponent {
    @Input() type: AlertType = 'info';
    @Input() title = '';
    @Input() description = '';

    @Input() icon?: string;
    @Input() color = '';
    @Input() showDetailsButton = false;

    @Output() detailsButtonClicked = new EventEmitter<void>();

    get alertClasses(): string {
        if (this.color) {
            return this.color as string;
        }
        return 'alert-' + this.type;
    }

    get iconContainerClasses(): string {
        if (this.color) {
            return '';
        }
        return 'alert-icon-' + this.type;
    }

    get displayIcon(): string {
        if (this.icon) {
            return this.icon;
        }

        switch (this.type) {
            case 'warning':
                return 'warning️';
            case 'error':
                return 'error';
            default:
                return 'info';
        }
    }
}
