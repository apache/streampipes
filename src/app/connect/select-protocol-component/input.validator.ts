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
