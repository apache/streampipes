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
    selector: 'sp-label',
    templateUrl: './sp-label.component.html',
    styleUrls: ['./sp-label.component.scss'],
})
export class SpLabelComponent implements OnInit {
    @Input()
    labelText: string;

    _labelBackground: string;

    labelTextColor = '';

    ngOnInit(): void {}

    @Input()
    set labelBackground(labelBackground: string) {
        this._labelBackground = labelBackground;
        this.labelTextColor = this.generateContrastColor(labelBackground);
    }

    get labelBackground(): string {
        return this._labelBackground;
    }

    generateContrastColor(bgColor) {
        const color =
            bgColor.charAt(0) === '#' ? bgColor.substring(1, 7) : bgColor;
        const r = parseInt(color.substring(0, 2), 16);
        const g = parseInt(color.substring(2, 4), 16);
        const b = parseInt(color.substring(4, 6), 16);
        return r * 0.299 + g * 0.587 + b * 0.114 > 186 ? '#000' : '#FFF';
    }
}
