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

import { Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import { ColorService } from '../../services/color.service';

@Component({
    selector: 'sp-label-selection',
    templateUrl: './label-selection.component.html',
    styleUrls: ['./label-selection.component.css']
})
export class LabelSelectionComponent implements OnInit {

    @Output() selectedLabelChange: EventEmitter<{category, name}> = new EventEmitter<{category, name}>();

    @Input()
    set labels(labels) {
        this._labels = labels;
        this.setup();
    }

    public _labels;
    public _selectedLabel: {category, name};
    public _categories;
    public _selectedCategory: string;

    private selectable = true;
    private removable = false;

    constructor(public colorService: ColorService) {
    }

    ngOnInit(): void {
    }

    setup() {
        this._categories = Object.keys(this._labels);
        this._selectedCategory = this._categories[0];
        this._selectedLabel = {category: this._selectedCategory, name: this._labels[this._selectedCategory][0]};
        this.selectedLabelChange.emit(this._selectedLabel);
    }

    selectLabel(label: {category, name}) {
        this._selectedLabel = label;
        this.selectedLabelChange.emit(this._selectedLabel);
    }
}
