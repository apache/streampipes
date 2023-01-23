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
import { Label } from '@streampipes/platform-services';
import { LabelService } from '../../services/label.service';

@Component({
    selector: 'sp-label-list-item',
    templateUrl: './label-list-item.component.html',
    styleUrls: ['./label-list-item.component.css'],
})
export class LabelListItemComponent {
    @Input()
    label: Label;

    @Output() removeLabel = new EventEmitter<Label>();

    constructor(public labelService: LabelService) {}

    public clickRemoveLabel() {
        this.removeLabel.emit(this.label);
    }

    public updateLabelName(newLabelName) {
        this.label.name = newLabelName;
        this.updateLabel();
    }

    public updateLabelColor(newLabelColor) {
        this.label.color = newLabelColor;
        this.updateLabel();
    }

    private updateLabel() {
        this.labelService.updateLabel(this.label).subscribe((res: Label) => {
            this.label = res;
        });
    }
}
