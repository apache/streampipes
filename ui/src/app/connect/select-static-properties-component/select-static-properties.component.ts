/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import { Component, Input, Output, EventEmitter } from '@angular/core';

import { StaticProperty } from '../model/StaticProperty';
import { DataSetDescription } from '../model/DataSetDescription';
import {EventSchema} from '../schema-editor/model/EventSchema';
import {ConfigurationInfo} from "../model/message/ConfigurationInfo";
@Component({
  selector: 'app-select-static-properties',
  templateUrl: './select-static-properties.component.html',
  styleUrls: ['./select-static-properties.component.css'],
})
export class SelectStaticPropertiesComponent {


  @Input()
  adapterId: string;

  @Input()
  staticProperties: StaticProperty[];
  @Input()
  eventSchema: EventSchema;
  @Output()
  validateEmitter = new EventEmitter();
  @Output()
  emitter: EventEmitter<any> = new EventEmitter<any>();

  completedStaticProperty: ConfigurationInfo;

  validateText() {
    if (this.staticProperties.every(this.allValid)) {
      this.validateEmitter.emit(true);
    } else {
      this.validateEmitter.emit(false);
    }
  }

  allValid(staticProperty) {
    return staticProperty.isValid;
  }

  triggerUpdate(configurationInfo: ConfigurationInfo) {
    this.completedStaticProperty = Object.assign({}, configurationInfo);
  }
}
