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

export class CustomOutputValidatorDirective {

    staticProperty: any;
    restrict: any;
    require: any;

    constructor() {
        this.restrict = 'A';
        this.require = 'ngModel';
    }

    link(scope, elm, attrs, ctrl) {
        var validator = (value) => {
            ctrl.$setValidity('customOutputValidator', value.length > 0);
            return value;
        };
        ctrl.$parsers.unshift(validator);
        ctrl.$formatters.unshift(validator);
    }
}