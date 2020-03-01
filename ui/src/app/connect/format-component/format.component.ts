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

import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormatDescription} from '../model/connect/grounding/FormatDescription';
import {ShepherdService} from '../../services/tour/shepherd.service';

@Component({
  selector: 'app-format',
  templateUrl: './format.component.html',
  styleUrls: ['./format.component.css'],
})
export class FormatComponent {
  @Input()
  format: FormatDescription;
  @Input()
  selectedFormat: FormatDescription;
  @Output()
  validateEmitter = new EventEmitter();
  @Output()
  editableEmitter = new EventEmitter();
  @Output()
  selectedFormatEmitter = new EventEmitter();

  hasConfig: Boolean;

  constructor(private ShepherdService: ShepherdService) {
    this.hasConfig = true;
  }

  formatEditable() {
    this.selectedFormat = this.format;
    this.selectedFormatEmitter.emit(this.selectedFormat);

    this.ShepherdService.trigger("select-" + this.selectedFormat.label.toLocaleLowerCase());

  }

  validateText(textValid) {
    if (textValid && this.format.edit) {
      this.validateEmitter.emit(true);
      this.selectedFormat = this.format;
      this.selectedFormatEmitter.emit(this.selectedFormat);
    } else {
      this.validateEmitter.emit(false);
      this.selectedFormat = null;
    }
  }
  isSelected(): boolean {
    if (!this.selectedFormat || !this.format) {
      return false;
    } else {
        return this.selectedFormat.label === this.format.label;
    }
  }
  ngOnInit() {}
}
