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

export class CollectionController {

    staticProperty: any;

    constructor() {

    }

    $onInit() {
        console.log(this.staticProperty);
    }

    addMember(sp) {
        console.log(sp);
        let newMember = angular.copy(this.staticProperty.properties.staticPropertyTemplate);
        newMember.properties.elementId = newMember.properties.elementId.concat(this.makeId());
        sp.properties.members.push(newMember);
    }

    removeMember(sp, index) {
        sp.properties.members.splice(index, 1);
    }

    makeId() {
        var text = "";
        var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for (var i = 0; i < 5; i++)
            text += possible.charAt(Math.floor(Math.random() * possible.length));

        return text;
    }
}