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

import {Component, EventEmitter, Input, Output} from '@angular/core';
import {ConfigurationInfo} from '../../model/message/ConfigurationInfo';
import {CollectionStaticProperty} from '../../model/CollectionStaticProperty';
import {EventSchema} from '../../schema-editor/model/EventSchema';
import {StaticPropertyUtilService} from '../static-property-util.service';


@Component({
    selector: 'app-static-collection',
    templateUrl: './static-collection.component.html',
    styleUrls: ['./static-collection.component.css']
})
export class StaticCollectionComponent {

    @Input() staticProperty: CollectionStaticProperty;
    @Input() adapterId: string;
    @Input() eventSchema: EventSchema;

    @Output() inputEmitter: EventEmitter<Boolean> = new EventEmitter<Boolean>();
    @Output() updateEmitter: EventEmitter<ConfigurationInfo> = new EventEmitter();



    private hasInput: Boolean;

    constructor(private staticPropertyUtil: StaticPropertyUtilService) {

    }


    valueChange(inputValue) {
        if ((<CollectionStaticProperty> this.staticProperty).members !== undefined) {
          let property = (<CollectionStaticProperty> this.staticProperty).members.find(member => member.isValid == false);
          property === undefined ? this.hasInput = true : this.hasInput = false;
        } else {
          this.hasInput = false;
        }

        this.inputEmitter.emit(this.hasInput);
        this.emitUpdate(this.hasInput);
    }

    emitUpdate(valid) {
        this.updateEmitter.emit(new ConfigurationInfo(this.staticProperty.internalName, valid));
    }

    add() {
        if (   (<CollectionStaticProperty> (this.staticProperty)).members === undefined) {
            (<CollectionStaticProperty> (this.staticProperty)).members = [];
        }
        let clone = this.staticPropertyUtil.clone((<CollectionStaticProperty> (this.staticProperty)).staticPropertyTemplate);
        (<CollectionStaticProperty> (this.staticProperty)).members.push(clone)
    }

    remove(i) {
        (<CollectionStaticProperty> (this.staticProperty)).members.splice(i,1).slice(0);
    }

}