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
import {
  AdapterDescriptionUnion,
  GenericAdapterSetDescription,
  GenericAdapterStreamDescription,
  ProtocolDescription
} from '../../../core-model/gen/streampipes-model';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatStepper } from '@angular/material/stepper';

@Component({
  selector: 'sp-generic-adapter-configuration',
  templateUrl: './generic-adapter-configuration.component.html',
  styleUrls: ['./generic-adapter-configuration.component.css']
})
export class GenericAdapterConfigurationComponent implements OnInit {

  /**
   * Adapter description the selected format is added to
   */
  @Input() adapterDescription: AdapterDescriptionUnion;

  /**
   * Cancels the adapter configuration process
   */
  @Output() removeSelectionEmitter: EventEmitter<boolean> = new EventEmitter();

  /**
   * Go to next configuration step when this is complete
   */
  @Output() clickNextEmitter: EventEmitter<MatStepper> = new EventEmitter();


  genericAdapterSettingsFormValid: boolean;

  genericAdapterForm: FormGroup;

  protocolDescription: ProtocolDescription;

  constructor(
    private _formBuilder: FormBuilder
  ) { }

  ngOnInit(): void {

    if (this.adapterDescription instanceof GenericAdapterSetDescription ||
      this.adapterDescription instanceof GenericAdapterStreamDescription) {
      this.protocolDescription = this.adapterDescription.protocolDescription;
    }

    // initialize form for validation
    this.genericAdapterForm = this._formBuilder.group({});
    this.genericAdapterForm.statusChanges.subscribe((status) => {
      this.genericAdapterSettingsFormValid = this.genericAdapterForm.valid;
    });
  }

  public removeSelection() {
    this.removeSelectionEmitter.emit();
  }

  public clickNext() {
    this.clickNextEmitter.emit();
  }
}
