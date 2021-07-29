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

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {EventPropertyUnion} from "../../../../../core-model/gen/streampipes-model";

@Component({
  selector: 'sp-select-properties',
  templateUrl: './select-properties.component.html',
  styleUrls: ['./select-properties.component.css']
})
export class SelectPropertiesComponent implements OnInit {

  @Output()
  changeSelectedProperties: EventEmitter<EventPropertyUnion[]> = new EventEmitter();

  @Input()
  availableProperties: EventPropertyUnion[];

  @Input()
  selectedProperties: EventPropertyUnion[];

  @Input()
  label: string;

  @Input()
  multiple: boolean

  constructor() { }

  ngOnInit(): void {
    console.log(this.selectedProperties);
  }

  triggerSelectedProperties(properties: EventPropertyUnion[]) {
    this.changeSelectedProperties.emit(properties);
  }

  compare(available: EventPropertyUnion, selected: EventPropertyUnion) {
    return available.runtimeName === selected.runtimeName;
  }

}
