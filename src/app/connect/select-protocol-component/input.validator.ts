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

import { Directive } from '@angular/core';
import {ValidatorFn, AbstractControl} from '@angular/forms';

export function ValidateUrl(control: AbstractControl) {
  if(control.value == null){
    return { validUrl: true };
  }
  else if (!control.value.match(/(http(s)?:\/\/.)?(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)/g)) {
    return { validUrl: true };
  }
  return null;
}

export function ValidateNumber(control: AbstractControl) {
  if(control.value == null){
    return { validUrl: true };
  }
  else if (isNaN(control.value)) {
    return { validUrl: true };
  }
  return null;
}

export function ValidateString(control: AbstractControl) {
  if(control.value == null){
    return { validUrl: true };
  }
  else if (!control.value.includes("@")) {
    return { validUrl: true };
  }
  return null;
}
