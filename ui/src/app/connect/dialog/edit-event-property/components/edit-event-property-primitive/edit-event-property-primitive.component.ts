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

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {DataTypesService} from '../../../../services/data-type.service';

@Component({
  selector: 'sp-edit-event-property-primitive',
  templateUrl: './edit-event-property-primitive.component.html',
  styleUrls: ['./edit-event-property-primitive.component.scss']
})
export class EditEventPropertyPrimitiveComponent implements OnInit {

  @Input() cachedProperty: any;
  @Input() index: number;
  @Input() isTimestampProperty: boolean;
  @Output() isNumericDataType = new EventEmitter<boolean>();

  hideUnitTransformation: boolean;

  addedByUser: boolean;

  constructor(private dataTypesService: DataTypesService) {
  }

  ngOnInit(): void {
    this.setShowUnitTransformation();
    this.addedByUser = this.staticValueAddedByUser();
    this.cachedProperty.staticValue = '';
  }

  setShowUnitTransformation() {
    this.hideUnitTransformation = this.isTimestampProperty ||
      !this.dataTypesService.isNumeric(this.cachedProperty.runtimeType);

    if(this.dataTypesService.isNumeric(this.cachedProperty.runtimeType)) {
      this.isNumericDataType.emit(true);
    } else {
      this.isNumericDataType.emit(false);
    }
  }

  staticValueAddedByUser() {
    if (this.cachedProperty.elementId.startsWith('http://eventProperty.de/staticValue/')) {
      return true;
    } else {
      return false;
    }
  }

}
