/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Component, Input, OnInit } from '@angular/core';

@Component({
    selector: 'sp-point-selection-info',
    templateUrl: './point-selection-info.component.html',
    styleUrls: ['./point-selection-info.component.css']
})
export class PointSelectionInfoComponent implements OnInit {

    @Input()
    set startX(startX) {
        this._startX = startX;
    }

    @Input()
    set endX(endX) {
        this._endX = endX;
    }

    @Input()
    set n_selected_points(n_selected_points) {
        this._n_selected_points = n_selected_points;
    }

    public _startX: string;
    public _endX: string;
    public _n_selected_points: number;

    constructor() {
    }

    ngOnInit(): void {
    }
}
