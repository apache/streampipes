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
    EventPropertyUnion,
    SemanticTypesService,
} from '@streampipes/platform-services';
import {
    debounceTime,
    distinctUntilChanged,
    startWith,
    switchMap,
} from 'rxjs/operators';
import { UntypedFormControl } from '@angular/forms';
import { Observable } from 'rxjs';

@Component({
    selector: 'sp-edit-schema-transformation',
    templateUrl: './edit-schema-transformation.component.html',
    styleUrls: ['./edit-schema-transformation.component.scss'],
})
export class EditSchemaTransformationComponent implements OnInit {
    soTimestamp = 'http://schema.org/DateTime';

    @Input()
    cachedProperty: any;

    @Input() isTimestampProperty: boolean;
    @Input() isNestedProperty: boolean;
    @Input() isListProperty: boolean;
    @Input() isPrimitiveProperty: boolean;

    @Output() dataTypeChanged = new EventEmitter<boolean>();
    @Output() timestampSemanticsChanged = new EventEmitter<boolean>();

    domainPropertyControl = new UntypedFormControl();
    semanticTypes: Observable<string[]>;

    constructor(private semanticTypesService: SemanticTypesService) {}

    ngOnInit(): void {
        this.semanticTypes = this.domainPropertyControl.valueChanges.pipe(
            startWith(''),
            debounceTime(400),
            distinctUntilChanged(),
            switchMap(val => {
                return val
                    ? this.semanticTypesService.getSemanticTypes(val)
                    : [];
            }),
        );
    }

    editTimestampDomainProperty(checked: boolean) {
        if (checked) {
            this.isTimestampProperty = true;
            this.cachedProperty.domainProperties = [this.soTimestamp];
            this.cachedProperty.propertyScope = 'HEADER_PROPERTY';
            this.cachedProperty.runtimeType =
                'http://www.w3.org/2001/XMLSchema#long';
        } else {
            this.cachedProperty.domainProperties = [];
            this.cachedProperty.propertyScope = 'MEASUREMENT_PROPERTY';
            this.isTimestampProperty = false;
        }
        this.timestampSemanticsChanged.emit(this.isTimestampProperty);
    }
}
