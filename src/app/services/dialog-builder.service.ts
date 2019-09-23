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

import * as angular from 'angular';

export class DialogBuilder {

    constructor() {

    }

    // TODO: Can't resolve
    getDialogTemplate(controller, template) {
        return {
            controller: controller,
            controllerAs: "ctrl",
            bindToController: true,
            template: template,
            parent: angular.element(document.body),
            clickOutsideToClose: false,
            //scope: this.$scope,
            //rootScope: this.$rootScope,
            //preserveScope: true
        }
    }
}
