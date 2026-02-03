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
    Component,
    EventEmitter,
    inject,
    Input,
    OnInit,
    Output,
} from '@angular/core';
import {
    DataType,
    EventPropertyPrimitive,
    SemanticType,
} from '@streampipes/platform-services';
import { MatSelectChange } from '@angular/material/select';
import { ShepherdService } from '../../../../../../services/tour/shepherd.service';

type PropertyScope =
    | 'TIMESTAMP_PROPERTY'
    | 'HEADER_PROPERTY'
    | 'MEASUREMENT_PROPERTY'
    | 'DIMENSION_PROPERTY';

@Component({
    selector: 'sp-event-property-scope',
    templateUrl: './event-property-scope.component.html',
    standalone: false,
})
export class EventPropertyScopeComponent implements OnInit {
    private shepherdService = inject(ShepherdService);

    @Input()
    eventProperty: EventPropertyPrimitive;

    @Input()
    runtimeType: string;

    @Input()
    label: string;

    @Input()
    isNumber: boolean;

    @Output()
    scopeChanged: EventEmitter<void> = new EventEmitter();

    currentScope: PropertyScope;

    ngOnInit() {
        this.determineCurrentScope();
    }

    onSelectionChange(event: MatSelectChange) {
        if (event.value === 'TIMESTAMP_PROPERTY') {
            this.eventProperty.propertyScope = 'HEADER_PROPERTY';
            this.eventProperty.semanticType = SemanticType.TIMESTAMP;
            if (!this.eventProperty.additionalMetadata.originType) {
                this.eventProperty.additionalMetadata.originType =
                    this.eventProperty.runtimeType;
            }
            this.eventProperty.runtimeType = DataType.LONG;
            this.shepherdService.trigger('timestamp-property-selected');
        } else {
            if (this.currentScope === 'TIMESTAMP_PROPERTY') {
                this.eventProperty.semanticType = undefined;
                this.eventProperty.runtimeType =
                    this.eventProperty.additionalMetadata.originType;
                this.eventProperty.additionalMetadata.originType = undefined;
            }
            this.eventProperty.propertyScope = event.value;
        }
        this.determineCurrentScope();
        this.scopeChanged.emit();
    }

    determineCurrentScope(): void {
        if (SemanticType.isTimestamp(this.eventProperty)) {
            this.currentScope = 'TIMESTAMP_PROPERTY';
        } else {
            this.currentScope = this.eventProperty
                .propertyScope as PropertyScope;
        }
    }
}
