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

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormatDescription } from '../../../core-model/gen/streampipes-model';

@Component({
    selector: 'sp-format-form',
    templateUrl: './format-form.component.html',
    styleUrls: ['./format-form.component.css']
})
export class FormatFormComponent implements OnInit {

    @Input() selectedFormat: FormatDescription;

    @Output() selectedFormatChange = new EventEmitter<FormatDescription>();
    @Output() inputValueChanged = new EventEmitter<boolean>();
    @Output() selectedFormatEmitter = new EventEmitter();
    @Input() allFormats: FormatDescription[];

    showStaticProperty: boolean[] = [false]
    hasInput: boolean[] = [false];

    constructor() {
    }

    isSelected(f: FormatDescription): boolean {
        if (this.selectedFormat === undefined) {
            return false;
        } else {
            this.selectedFormatChange.emit(this.selectedFormat);
            return f.name === this.selectedFormat.name;
        }
    }

    ngOnInit() {
        const selectedFormat = this.selectedFormat;

        setTimeout(() => {
            for (var i = 0; i < this.allFormats.length ; i++) {
                if (selectedFormat && this.allFormats[i].name === selectedFormat.name) {
                    this.showStaticProperty.push(true);
                    this.hasInput.push(true);
                } else {
                    this.showStaticProperty.push(false);
                    this.hasInput.push(false);
                }

            }
        }, 30);

    }

    validateAll(allValid) {
        this.inputValueChanged.emit(allValid);
    }

    formatSelected(selectedFormat) {
        this.selectedFormat = selectedFormat;
        this.selectedFormatEmitter.emit(this.selectedFormat)

    }

}
