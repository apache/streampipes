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

import {StaticProperty} from "../../../core-model/gen/streampipes-model";
import {AbstractStaticPropertyRenderer} from "./abstract-static-property";
import {FormControl, ValidatorFn} from "@angular/forms";
import {OnDestroy} from "@angular/core";

export abstract class AbstractValidatedStaticPropertyRenderer<T extends StaticProperty>
    extends AbstractStaticPropertyRenderer<T> implements OnDestroy {

  errorMessage = "Please enter a value";
  fieldValid: boolean;

  constructor() {
    super();
  }

  enableValidators() {
    this.parentForm.controls[this.fieldName].valueChanges.subscribe(value => {
     this.onValueChange(value);
    });
    this.parentForm.controls[this.fieldName].statusChanges.subscribe(status => {
      this.fieldValid = status === "VALID";
      this.onStatusChange(status);
    })
  }

  addValidator(defaultValue: any, validators: ValidatorFn | ValidatorFn[]) {
    this.parentForm.addControl(this.fieldName, new FormControl(defaultValue, validators));
    this.parentForm.updateValueAndValidity();
  }


  abstract onValueChange(value: any);

  abstract onStatusChange(status: any);

  ngOnDestroy(): void {
    if (this.parentForm) {
      this.parentForm.removeControl(this.fieldName);
    }
  }
}