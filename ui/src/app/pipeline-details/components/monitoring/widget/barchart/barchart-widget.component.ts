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
    selector: 'sp-barchart-widget',
    templateUrl: './barchart-widget.component.html',
    styleUrls: ['./barchart-widget.component.scss'],
})
export class BarchartWidgetComponent implements OnInit {
    _data = [];

    @Input()
    backgroundColor = '#cccccc';

    @Input()
    textColor = '#1b1464';

    colorScheme = {
        domain: ['#1b1464'],
    };

    ngOnInit(): void {
        this.colorScheme.domain = [this.textColor];
    }

    @Input()
    set data(data) {
        this._data = data;
    }

    get data() {
        return this._data;
    }
}
