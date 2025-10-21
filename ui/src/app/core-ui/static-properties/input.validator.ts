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

import {
    AbstractControl,
    AsyncValidatorFn,
    ValidationErrors,
    ValidatorFn,
} from '@angular/forms';
import {
    AdapterService,
    DatalakeRestService,
    PipelineService,
} from '@streampipes/platform-services';
import {
    catchError,
    debounceTime,
    distinctUntilChanged,
    map,
    Observable,
    of,
    switchMap,
    tap,
} from 'rxjs';

export function ValidateUrl(control: AbstractControl) {
    if (control.value == null) {
        return { validUrl: true };
    } else if (
        !control.value.match(
            /(http(s)?:\/\/.)?(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)/g,
        )
    ) {
        return { validUrl: true };
    }
    return null;
}

export function ValidateNumber(control: AbstractControl) {
    if (control.value == null || control.value == '') {
        return null;
    }

    if (!isNaN(parseFloat(control.value)) && isFinite(control.value)) {
        return null;
    }

    return { validNumber: true };
}

export function ValidateString(control: AbstractControl) {
    if (control.value == null) {
        return { validString: true };
    }
    return null;
}

export function checkForDuplicatesValidator(
    getExistingNames: () => string[],
): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
        if (!control.value || typeof control.value !== 'string') {
            return null;
        }
        const existingNames = getExistingNames();

        const isDuplicate = existingNames.includes(control.value);

        return isDuplicate ? { forbiddenName: { value: control.value } } : null;
    };
}

export function ValidateName(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
        const value = control.value;

        if (value == null) {
            return null;
        }

        const trimmed = value.trim();

        if (trimmed.length === 0) {
            return { whiteSpaceOnly: { value } };
        }

        // value starts or ends with whitespace
        if (/^\s|\s$/.test(value)) {
            return { leadingOrTrailingWhitespace: { value } };
        }

        // Matches strings containing only Unicode letters, numbers,
        // punctuation, symbols, and whitespace (including spaces)
        const regex = /^[\p{L}\p{N}\p{P}\p{S}\s]+$/u;
        const valid = regex.test(trimmed);

        return valid ? null : { invalidName: { value } };
    };
}

export function nameAsyncValidator(
    service: AdapterService | PipelineService | DatalakeRestService,
    orgName: string = '',
): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors | null> => {
        const name = control.value;

        if (name === orgName && orgName != '') {
            return of(null);
        }

        if (!name || !name.trim()) {
            return of(null); // skip validation if empty
        }

        return of(name).pipe(
            debounceTime(300),
            distinctUntilChanged(),
            switchMap(adapterName =>
                service.validateName(adapterName).pipe(
                    map((isUnique: boolean) => {
                        if (!isUnique) {
                            return { nameNotUnique: true };
                        }
                        return null; // no error
                    }),
                ),
            ),
        );
    };
}
