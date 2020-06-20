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
 */

import { Component, EventEmitter, HostListener, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'sp-image-bar',
  templateUrl: './image-bar.component.html',
  styleUrls: ['./image-bar.component.css']
})
export class ImageBarComponent implements OnInit {

  @Input() imagesSrcs: string [];
  @Input() selectedIndex: number;
  @Input() enableShortCuts: boolean;

  @Output() indexChange: EventEmitter<number> = new EventEmitter<number>();
  @Output() pageUp: EventEmitter<void> = new EventEmitter<void>();
  @Output() pageDown: EventEmitter<void> = new EventEmitter<void>();


  constructor() { }

  ngOnInit(): void {
  }

  changeImage(index) {
    this.indexChange.emit(index);
  }

  previousPage() {
    this.pageDown.emit();

  }
  previousImage() {
    if (this.selectedIndex < this.imagesSrcs.length - 1) {
      this.indexChange.emit(this.selectedIndex + 1);
    } else {
      this.pageDown.emit();
    }
  }
  nextImage() {
    if (this.selectedIndex > 0) {
      this.indexChange.emit(this.selectedIndex - 1);
    } else {
      this.pageUp.emit();
    }
  }

  nextPage() {
   this.pageUp.emit();
  }

  @HostListener('document:keydown', ['$event'])
  handleShortCuts(event: KeyboardEvent) {
    if (this.enableShortCuts && !event.repeat) {
      const key = event.key;
      switch (key.toLowerCase()) {
        case 'q':
          this.nextImage();
          break;
        case 'e':
          this.previousImage();
          break;
      }
    }
  }

}
