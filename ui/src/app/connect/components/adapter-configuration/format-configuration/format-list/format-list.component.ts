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
import { FormatDescription } from '@streampipes/platform-services';
import { RestService } from '../../../../services/rest.service';

@Component({
    selector: 'sp-format-list',
    templateUrl: './format-list.component.html',
    styleUrls: ['./format-list.component.scss'],
})
export class FormatListComponent implements OnInit {
    @Input() public selectedFormat: FormatDescription;

    @Output() validateEmitter = new EventEmitter();
    @Output() public selectedFormatEmitter =
        new EventEmitter<FormatDescription>();

    /**
     * Contains all the available formats that a user can select from
     */
    allFormats: FormatDescription[] = [];

    allJsonFormats: FormatDescription[] = [];
    showJsonFormats: FormatDescription[] = [];

    jsonFormatDescription = new FormatDescription();
    selectJsonFormats = false;

    constructor(private restService: RestService) {
        this.jsonFormatDescription.name = 'Json';
    }

    ngOnInit(): void {
        // fetch all available formats from backend
        this.restService.getFormats().subscribe(res => {
            // this.allFormats.push(jsonFormatDescription);
            // split resulting formats up to only show one button for Json Formats
            res.forEach(format => {
                if (format.formatType !== 'json') {
                    this.allFormats.push(format);
                } else {
                    this.allJsonFormats.push(format);
                }
            });
        });
    }

    formatEditable(selectedFormat) {
        this.allFormats.forEach(format => {
            if (format !== selectedFormat) {
                (format as any).edit = false;
            }
        });
    }

    formatSelected(selectedFormat: FormatDescription) {
        if (selectedFormat.formatType !== 'json') {
            this.showJsonFormats = [];
            this.selectJsonFormats = false;
        }

        this.selectedFormat = selectedFormat;
        this.selectedFormatEmitter.emit(this.selectedFormat);
    }

    selectJsonFormat() {
        this.showJsonFormats = this.allJsonFormats;
        this.selectJsonFormats = true;

        this.selectedFormat = this.allJsonFormats[2];
        this.selectedFormatEmitter.emit(this.selectedFormat);
    }

    validateAll(allValid) {
        this.validateEmitter.emit(allValid);
    }
}
