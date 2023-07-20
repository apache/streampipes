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

import { XsService } from '../../NS/xs.service';
import { ConfigurationInfo } from '../../connect/model/ConfigurationInfo';
import {
    AnyStaticProperty,
    CodeInputStaticProperty,
    CollectionStaticProperty,
    ColorPickerStaticProperty,
    EventSchema,
    FileStaticProperty,
    FreeTextStaticProperty,
    MappingPropertyNary,
    MappingPropertyUnary,
    OneOfStaticProperty,
    RuntimeResolvableAnyStaticProperty,
    RuntimeResolvableOneOfStaticProperty,
    RuntimeResolvableTreeInputStaticProperty,
    SecretStaticProperty,
    SlideToggleStaticProperty,
    StaticProperty,
    StaticPropertyAlternatives,
    StaticPropertyGroup,
} from '@streampipes/platform-services';
import { UntypedFormGroup } from '@angular/forms';
import { InvocablePipelineElementUnion } from '../../editor/model/editor.model';

@Component({
    selector: 'sp-app-static-property',
    templateUrl: './static-property.component.html',
    styleUrls: ['./static-property.component.css'],
    providers: [XsService],
})
export class StaticPropertyComponent implements OnInit {
    @Input()
    staticProperty: StaticProperty;

    @Input()
    staticProperties: StaticProperty[];

    @Input()
    adapterId: string;

    @Output()
    validateEmitter: EventEmitter<any> = new EventEmitter<any>();

    @Output()
    updateEmitter: EventEmitter<ConfigurationInfo> = new EventEmitter();

    @Input()
    eventSchemas: EventSchema[];

    @Input()
    completedStaticProperty: ConfigurationInfo;

    @Input()
    parentForm: UntypedFormGroup;

    @Input()
    fieldName: string;

    @Input()
    displayRecommended: boolean;

    @Input()
    pipelineElement: InvocablePipelineElementUnion;

    showLabel = true;

    @Input()
    showBorder = true;

    constructor() {}

    ngOnInit() {
        this.showLabel =
            (!(this.staticProperty instanceof StaticPropertyGroup) ||
                (this.staticProperty as StaticPropertyGroup).showLabel) &&
            this.staticProperty.label !== '';
    }

    isCodeInputStaticProperty(val) {
        return val instanceof CodeInputStaticProperty;
    }

    isFreeTextStaticProperty(val) {
        return val instanceof FreeTextStaticProperty;
    }

    isFileStaticProperty(val) {
        return val instanceof FileStaticProperty;
    }

    isAnyStaticProperty(val) {
        return val instanceof AnyStaticProperty;
    }

    isMappingPropertyUnary(val) {
        return val instanceof MappingPropertyUnary;
    }

    isOneOfStaticProperty(val) {
        return val instanceof OneOfStaticProperty;
    }

    isMappingNaryProperty(val) {
        return val instanceof MappingPropertyNary;
    }

    isRuntimeResolvableOneOfStaticProperty(val) {
        return val instanceof RuntimeResolvableOneOfStaticProperty;
    }

    isSecretStaticProperty(val) {
        return val instanceof SecretStaticProperty;
    }

    isColorPickerStaticProperty(val) {
        return val instanceof ColorPickerStaticProperty;
    }

    isRuntimeResolvableAnyStaticProperty(val) {
        return val instanceof RuntimeResolvableAnyStaticProperty;
    }

    isGroupStaticProperty(val) {
        return val instanceof StaticPropertyGroup;
    }

    isAlternativesStaticProperty(val) {
        return val instanceof StaticPropertyAlternatives;
    }

    isCollectionStaticProperty(val) {
        return val instanceof CollectionStaticProperty;
    }

    isSlideToggleStaticProperty(val) {
        return val instanceof SlideToggleStaticProperty;
    }

    isTreeInputStaticProperty(val) {
        return val instanceof RuntimeResolvableTreeInputStaticProperty;
    }

    valueChange(hasInput) {
        this.validateEmitter.emit();
    }

    emitUpdate(configurationInfo: ConfigurationInfo) {
        this.updateEmitter.emit(configurationInfo);
    }
}
