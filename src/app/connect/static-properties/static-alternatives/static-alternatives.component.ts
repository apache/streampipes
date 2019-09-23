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

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {AlternativesStaticProperty} from '../../model/AlternativesStaticProperty';
import {EventSchema} from '../../schema-editor/model/EventSchema';
import {GroupStaticProperty} from '../../model/GroupStaticProperty';

@Component({
    selector: 'app-static-alternatives',
    templateUrl: './static-alternatives.component.html',
    styleUrls: ['./static-alternatives.component.css']
})
export class StaticAlternativesComponent implements OnInit {


    @Input()
    staticProperty: AlternativesStaticProperty;

    @Input()
    adapterId: string;

    @Input()
    eventSchema: EventSchema;

    @Output() inputEmitter: EventEmitter<Boolean> = new EventEmitter<Boolean>();

    private hasInput: Boolean;
    private errorMessage = "Please select a option";

    ngOnInit(): void {
        this.staticProperty.alternatives.forEach( alternative => alternative.selected = false);
        // TODO: Remove hack
        if (this.staticProperty.alternatives.length === 1) {
            this.hasInput = true;
            this.inputEmitter.emit(this.hasInput);
        }
    }

    valueChange(inputValue) {
        // TODO: Remove hack
        if (this.staticProperty.alternatives.length === 1) {
            this.hasInput = true;
        } else {

            this.hasInput = false;
            let alternative = this.staticProperty.alternatives.find(alternative => alternative.selected == true);
            if (alternative !== undefined) {
                if (alternative.staticProperty !== undefined) {
                    var childsAreValid = true;
                    (<GroupStaticProperty> alternative.staticProperty).staticProperties.forEach(property => {
                        if (!property.isValid) {
                            childsAreValid = false
                        }
                    });
                    this.hasInput = childsAreValid;
                } else {
                    this.hasInput = true;
                }
            }
        }
        this.inputEmitter.emit(this.hasInput);
    }

    radioSelectionChange(event) {
        this.staticProperty.alternatives.forEach(alternative => {
            if (alternative.id == event.value.id) {
                alternative.selected = true;
            } else {
                alternative.selected = false;
            }
        });
        this.valueChange(event);
    }

}