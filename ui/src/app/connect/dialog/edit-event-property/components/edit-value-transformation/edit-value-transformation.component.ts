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

import { Component, Input, OnInit } from '@angular/core';
import { StaticValueTransformService } from '../../../../services/static-value-transform.service';
import {
    EventPropertyUnion,
    TransformationRuleDescription,
} from '@streampipes/platform-services';

@Component({
    selector: 'sp-edit-value-transformation',
    templateUrl: './edit-value-transformation.component.html',
    styleUrls: ['./edit-value-transformation.component.scss'],
})
export class EditValueTransformationComponent implements OnInit {
    @Input()
    cachedProperty: EventPropertyUnion;

    @Input() originalProperty: EventPropertyUnion;
    @Input() transformationRules: TransformationRuleDescription[] = [];

    @Input() isTimestampProperty: boolean;
    @Input() isNestedProperty: boolean;
    @Input() isListProperty: boolean;
    @Input() isPrimitiveProperty: boolean;
    @Input() isNumericProperty: boolean;
    @Input() isStringProperty: boolean;

    addedByUser: boolean;
    staticValue: string;

    constructor(
        private staticValueTransformService: StaticValueTransformService,
    ) {}

    ngOnInit(): void {
        this.addedByUser =
            this.staticValueTransformService.isStaticValueProperty(
                this.cachedProperty.elementId,
            );
        this.staticValue = this.staticValueTransformService.getStaticValue(
            this.cachedProperty.elementId,
        );
    }

    applyStaticValue(value: any) {
        this.cachedProperty.elementId =
            this.staticValueTransformService.makeElementId(value);
    }
}
