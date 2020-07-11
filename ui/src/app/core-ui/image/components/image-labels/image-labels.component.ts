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

import { Component, EventEmitter, HostListener, Input, OnInit, Output } from '@angular/core';
import { ColorService } from '../../services/color.service';

@Component({
  selector: 'sp-image-labels',
  templateUrl: './image-labels.component.html',
  styleUrls: ['./image-labels.component.css']
})
export class ImageLabelsComponent implements OnInit {

  @Input()
  set labels(labels) {
    this._labels = labels;
    this.update();
  }
  @Input() enableShortCuts: boolean;
  @Output() labelChange: EventEmitter<{category, label}> = new EventEmitter<{category, label}>();

  public _labels;
  public _selectedLabel: {category, label};
  public categories;
  public selectedCategory;

  constructor(public colorService: ColorService) { }

  ngOnInit(): void {

  }

  update() {
    this.categories = Object.keys(this._labels);
    this.selectedCategory = this.categories[0];
    this._selectedLabel = {category: this.selectedCategory, label: this._labels[this.selectedCategory][0]};
    this.labelChange.emit(this._selectedLabel);
  }

  selectLabel(e: {category, label}) {
    this._selectedLabel = e;
    this.labelChange.emit(this._selectedLabel);
  }

  @HostListener('document:keydown', ['$event'])
  handleShortCuts(event: KeyboardEvent) {
    if (this.enableShortCuts) {
      if (event.code.toLowerCase().includes('digit')) {
        // Number
        const value = Number(event.key);
        if (value !== 0 && value <= this._labels[this.selectedCategory].length) {
          this.selectLabel({category: this.selectedCategory, label: this._labels[this.selectedCategory][value - 1]});
        }
      }
    }
  }

}
