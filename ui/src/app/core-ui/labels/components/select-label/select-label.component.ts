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

import {
    Component,
    EventEmitter,
    HostListener,
    Input,
    OnInit,
    Output,
} from '@angular/core';
import { ColorService } from '../../../image/services/color.service';
import { Category, Label } from '@streampipes/platform-services';
import { LabelService } from '../../services/label.service';

@Component({
    selector: 'sp-select-label',
    templateUrl: './select-label.component.html',
    styleUrls: ['./select-label.component.css'],
})
export class SelectLabelComponent implements OnInit {
    public categories: Category[];
    public selectedCategory: Category;

    @Input() enableShortCuts: boolean;
    @Output() labelChange: EventEmitter<Label> = new EventEmitter<Label>();

    public labels: Label[];
    public selectedLabel: Label;

    public noCategories = true;

    constructor(
        public labelService: LabelService,
        public colorService: ColorService,
    ) {}

    ngOnInit(): void {
        this.labelService.getCategories().subscribe(res => {
            this.categories = res;
            if (this.categories.length > 0) {
                this.noCategories = false;
                this.selectedCategory = res[0];
                this.changeCategory(this.selectedCategory);
            }
        });
    }

    changeCategory(c) {
        this.labelService.getLabelsOfCategory(c).subscribe(res => {
            this.labels = res;
            this.selectLabel(this.labels[0]);
        });
    }

    selectLabel(e: Label) {
        this.selectedLabel = e;
        this.labelChange.emit(this.selectedLabel);
    }

    @HostListener('document:keydown', ['$event'])
    handleShortCuts(event: KeyboardEvent) {
        if (this.enableShortCuts) {
            if (event.code.toLowerCase().includes('digit')) {
                // Number
                const value = Number(event.key);
                if (value !== 0 && value <= this.labels.length) {
                    this.selectLabel(this.labels[value - 1]);
                }
            }
        }
    }
}
