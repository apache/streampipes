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
    debounceTime,
    distinctUntilChanged,
    startWith,
    switchMap,
} from 'rxjs/operators';
import { UntypedFormControl } from '@angular/forms';
import { Observable } from 'rxjs';
import { ShepherdService } from '../../../../../services/tour/shepherd.service';
import {
    DataType,
    SemanticType,
    SemanticTypesRestService,
} from '@streampipes/platform-services';
import { Router } from '@angular/router';

@Component({
    selector: 'sp-edit-schema-transformation',
    templateUrl: './edit-schema-transformation.component.html',
    styleUrls: ['./edit-schema-transformation.component.scss'],
})
export class EditSchemaTransformationComponent implements OnInit {
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

    adapterIsInEditMode: boolean;

    constructor(
        private semanticTypesRestService: SemanticTypesRestService,
        private shepherdService: ShepherdService,
        private router: Router,
    ) {}

    ngOnInit(): void {
        this.semanticTypes = this.domainPropertyControl.valueChanges.pipe(
            startWith(''),
            debounceTime(400),
            distinctUntilChanged(),
            switchMap(val => {
                return val
                    ? this.semanticTypesRestService.getSemanticTypes(val)
                    : [];
            }),
        );

        this.adapterIsInEditMode = this.router.url.includes('connect/edit');
    }

    editTimestampDomainProperty(checked: boolean) {
        if (checked) {
            this.isTimestampProperty = true;
            this.cachedProperty.domainProperties = [SemanticType.TIMESTAMP];
            this.cachedProperty.propertyScope = 'HEADER_PROPERTY';
            this.cachedProperty.runtimeType = DataType.LONG;
        } else {
            this.cachedProperty.domainProperties = [];
            this.cachedProperty.propertyScope = 'MEASUREMENT_PROPERTY';
            this.isTimestampProperty = false;
        }
        this.timestampSemanticsChanged.emit(this.isTimestampProperty);
    }

    triggerTutorialStep(): void {
        if (this.cachedProperty.runtimeName === 'temp') {
            this.shepherdService.trigger('adapter-runtime-name-changed');
        }
    }
}
