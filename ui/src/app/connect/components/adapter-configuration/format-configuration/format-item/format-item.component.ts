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
import { ShepherdService } from '../../../../../services/tour/shepherd.service';
import { FormatDescription } from '@streampipes/platform-services';

@Component({
    selector: 'sp-format-item',
    templateUrl: './format-item.component.html',
    styleUrls: ['./format-item.component.scss'],
})
export class FormatItemComponent {
    @Input()
    format: FormatDescription;
    @Input()
    selectedFormat: FormatDescription;
    @Output()
    validateEmitter = new EventEmitter();
    @Output()
    editableEmitter = new EventEmitter();
    @Output()
    selectedFormatEmitter = new EventEmitter();

    hasConfig: boolean;

    constructor(private shepherdService: ShepherdService) {
        this.hasConfig = true;
    }

    formatEditable() {
        this.selectedFormat = this.format;
        this.selectedFormatEmitter.emit(this.selectedFormat);

        this.shepherdService.trigger(
            'select-' + this.selectedFormat.name.toLocaleLowerCase(),
        );
    }

    isSelected(): boolean {
        if (!this.selectedFormat || !this.format) {
            return false;
        } else {
            return this.selectedFormat.name === this.format.name;
        }
    }
}
