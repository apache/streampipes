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

import { Component, DoCheck, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { EventProperty } from '../../../core-model/gen/streampipes-model';
import { DataTypesService } from '../../schema-editor/data-type.service';
import { DomainPropertyProbabilityList } from '../../schema-editor/model/DomainPropertyProbabilityList';

@Component({
  selector: 'sp-edit-event-property-primitive',
  templateUrl: './edit-event-property-primitive.component.html',
  styleUrls: ['./edit-event-property-primitive.component.css']
})
export class EditEventPropertyPrimitiveComponent implements OnInit {

  soTimestamp = 'http://schema.org/DateTime';

  @Input() cachedProperty: any;
  @Input() index: number;

  @Input() domainPropertyGuess: DomainPropertyProbabilityList;
  @Input() isEditable: boolean;

  @Output() delete: EventEmitter<EventProperty> = new EventEmitter<EventProperty>();
  @Output() addPrimitive: EventEmitter<EventProperty> = new EventEmitter<EventProperty>();
  @Output() addNested: EventEmitter<any> = new EventEmitter<any>();

  runtimeDataTypes;

  private selectedTimeMultiplier;
  isTimestampProperty: boolean;

  constructor(private formBuilder: FormBuilder,
    private dataTypeService: DataTypesService) {
    this.dataTypeService = dataTypeService;

    this.runtimeDataTypes = this.dataTypeService.getDataTypes();

    // Set preselected value
    this.selectedTimeMultiplier = 'second';
  }

  protected open = false;

  ngOnInit() {
    this.isTimestampProperty = this.cachedProperty.domainProperties.some(dp => dp === this.soTimestamp);
    // (this.cachedProperty as any).timestampTransformationMultiplier = 1000;
  }

  // ngDoCheck() {
    // (this.cachedProperty as any).propertyNumber = this.index;
  // }

  staticValueAddedByUser() {
    if (this.cachedProperty.elementId.startsWith('http://eventProperty.de/staticValue/')) {
      return true;
    } else {
      return false;
    }
  }

}
