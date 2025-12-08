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

import { Component, Input, OnInit } from '@angular/core';

@Component({
    selector: 'sp-form-label',
    templateUrl: './form-label.component.html',
    styleUrls: ['./form-label.component.scss'],
    standalone: false,
})
export class FormLabelComponent implements OnInit {
    @Input()
    level: 1 | 2 | 3 = 2;

    @Input()
    label: string;

    @Input()
    description: string;

    @Input()
    tooltip: string;

    margin = '';

    ngOnInit(): void {
        if (!this.margin && this.level === 1) {
            this.margin = '10px 0px';
        } else if (!this.margin && this.level === 2) {
            this.margin = '5px 0px';
        } else {
            this.margin = '2px 0px';
        }
    }
}
