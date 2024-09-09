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

import { Injectable } from '@angular/core';
import {
    AnyStaticProperty,
    CodeInputStaticProperty,
    CollectionStaticProperty,
    ColorPickerStaticProperty,
    FileStaticProperty,
    FreeTextStaticProperty,
    MappingPropertyNary,
    MappingPropertyUnary,
    OneOfStaticProperty,
    Option,
    RuntimeResolvableAnyStaticProperty,
    RuntimeResolvableOneOfStaticProperty,
    SecretStaticProperty,
    StaticProperty,
    StaticPropertyAlternative,
    StaticPropertyAlternatives,
    StaticPropertyGroup,
} from '@streampipes/platform-services';
import { IdGeneratorService } from '../../core-services/id-generator/id-generator.service';

@Injectable({ providedIn: 'root' })
export class StaticPropertyUtilService {
    constructor(private idGeneratorService: IdGeneratorService) {}

    public clone(val: StaticProperty) {
        let clone;
        const id = this.idGeneratorService.generatePrefixedId();
        if (val instanceof FreeTextStaticProperty) {
            clone = new FreeTextStaticProperty();
            clone.elementId = id;
            clone.value = val.value;
            clone.requiredDomainProperty = val.requiredDomainProperty;
        } else if (val instanceof FileStaticProperty) {
            clone = new FileStaticProperty();
            clone.elementId = id;
            clone.endpointUrl = val.endpointUrl;
            clone.locationPath = val.locationPath;
        } else if (val instanceof MappingPropertyUnary) {
            clone = new MappingPropertyUnary();
            clone.elementId = id;
            clone.requirementSelector = val.requirementSelector;
            clone.mapsFromOptions = val.mapsFromOptions;
            clone.propertyScope = val.propertyScope;
            clone.selectedProperty = val.selectedProperty;
        } else if (val instanceof MappingPropertyNary) {
            clone = new MappingPropertyNary();
            clone.elementId = id;
            clone.requirementSelector = val.requirementSelector;
            clone.mapsFromOptions = val.mapsFromOptions;
            clone.propertyScope = val.propertyScope;
            clone.selectedProperties = val.selectedProperties;
        } else if (val instanceof SecretStaticProperty) {
            clone = new SecretStaticProperty();
            clone.elementId = id;
            clone.value = val.value;
            clone.encrypted = val.encrypted;
        } else if (val instanceof ColorPickerStaticProperty) {
            clone = new ColorPickerStaticProperty();
            clone.id = id;
            clone.selectedProperty = val.selectedColor;
        } else if (val instanceof CodeInputStaticProperty) {
            clone = new CodeInputStaticProperty();
            clone.elementId = id;
            clone.codeTemplate = val.codeTemplate;
            clone.value = val.value;
            clone.language = val.language;
        } else if (val instanceof StaticPropertyGroup) {
            clone = new StaticPropertyGroup();
            clone.elementId = id;
            clone.staticProperties = val.staticProperties.map(elem =>
                this.clone(elem),
            );
            clone.showLabel = val.showLabel;
            clone.horizontalRendering = val.horizontalRendering;
        } else if (val instanceof StaticPropertyAlternatives) {
            clone = new StaticPropertyAlternatives();
            clone.elementId = id;
            clone.alternatives = val.alternatives.map(elem => this.clone(elem));
        } else if (val instanceof StaticPropertyAlternative) {
            clone = new StaticPropertyAlternative();
            clone.elementId = id;
            clone.selected = val.selected;
            clone.staticProperty = this.clone(val.staticProperty);
        } else if (val instanceof CollectionStaticProperty) {
            clone = new CollectionStaticProperty();
            clone.elementId = id;
            clone.staticPropertyTemplate = this.clone(
                val.staticPropertyTemplate,
            );
            clone.members = val.members.map(elem => this.clone(elem));
            clone.memberType = val.memberType;
        } else if (
            val instanceof RuntimeResolvableAnyStaticProperty ||
            val instanceof RuntimeResolvableOneOfStaticProperty
        ) {
            val instanceof RuntimeResolvableAnyStaticProperty
                ? (clone = new RuntimeResolvableAnyStaticProperty())
                : (clone = new RuntimeResolvableOneOfStaticProperty());

            clone.elementId = id;
            clone.dependsOn = val.dependsOn;
            clone.options = val.options.map(option => this.cloneOption(option));
            clone.horizontalRendering = val.horizontalRendering;
        } else if (
            val instanceof AnyStaticProperty ||
            val instanceof OneOfStaticProperty
        ) {
            val instanceof AnyStaticProperty
                ? (clone = new AnyStaticProperty())
                : (clone = new OneOfStaticProperty());

            clone.elementId = id;
            clone.options = val.options.map(option => this.cloneOption(option));
            clone.horizontalRendering = val.horizontalRendering;
        }
        clone = this.copyStaticPropertyProperty(val, clone);
        clone['@class'] = val['@class'];
        return clone;
    }

    private copyStaticPropertyProperty(
        src: StaticProperty,
        dst: StaticProperty,
    ): StaticProperty {
        dst.label = src.label;
        dst.description = src.description;
        dst.internalName = src.internalName;
        return dst;
    }

    private cloneOption(val: Option) {
        const clone = new Option();
        clone['@class'] = 'org.apache.streampipes.model.staticproperty.Option';
        clone.elementId = this.idGeneratorService.generatePrefixedId();
        clone.name = val.name;
        clone.internalName = val.internalName;
        clone.selected = val.selected;
        return clone;
    }

    public asFreeTextStaticProperty(
        val: StaticProperty,
    ): FreeTextStaticProperty {
        return val as FreeTextStaticProperty;
    }

    public asColorPickerStaticProperty(
        val: StaticProperty,
    ): ColorPickerStaticProperty {
        return val as ColorPickerStaticProperty;
    }
}
