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
import { StaticProperty } from '../model/StaticProperty';
import { FreeTextStaticProperty } from '../model/FreeTextStaticProperty';
import {SecretStaticProperty} from "../model/SecretStaticProperty";
import {CollectionStaticProperty} from '../model/CollectionStaticProperty';
import { FileStaticProperty } from "../model/FileStaticProperty";
import { AnyStaticProperty } from "../model/AnyStaticProperty";
import { MappingPropertyUnary } from "../model/MappingPropertyUnary";
import { OneOfStaticProperty } from "../model/OneOfStaticProperty";
import { MappingPropertyNary } from "../model/MappingPropertyNary";
import { RuntimeResolvableOneOfStaticProperty } from "../model/RuntimeResolvableOneOfStaticProperty";
import { RuntimeResolvableAnyStaticProperty } from "../model/RuntimeResolvableAnyStaticProperty";
import { GroupStaticProperty } from "../model/GroupStaticProperty";
import { AlternativesStaticProperty } from "../model/AlternativesStaticProperty";
import { AlternativeStaticProperty } from "../model/AlternativeStaticProperty";
import { Option } from "../model/Option";
import { URI } from "../model/URI";
import {ColorPickerStaticProperty} from "../model/ColorPickerStaticProperty";


@Injectable()
export class StaticPropertyUtilService{

    public clone(val: StaticProperty) {
        let clone;
        let id = 'urn:streampipes.org:spi::' + this.generateID(6);

        if (val instanceof FreeTextStaticProperty) {
            clone = new FreeTextStaticProperty();
            clone.id = id;
            clone.value = val.value;
            clone.requiredDomainProperty = val.requiredDomainProperty;
        }
        else if (val instanceof FileStaticProperty) {
            clone = new FileStaticProperty(id);
            clone.endpointUrl = val.endpointUrl;
            clone.locationPath = val.locationPath;
        }
        else if (val instanceof MappingPropertyUnary) {
            clone = new MappingPropertyUnary();
            clone.id = id;
            clone.requirementSelector = val.requirementSelector;
            clone.mapsFromOptions = val.mapsFromOptions;
            clone.propertyScope = val.propertyScope;
            clone.selectedProperty = val.selectedProperty;
        }
        else if (val instanceof MappingPropertyNary) {
            clone = new MappingPropertyNary();
            clone.id = id;
            clone.requirementSelector = val.requirementSelector;
            clone.mapsFromOptions = val.mapsFromOptions;
            clone.propertyScope = val.propertyScope;
            clone.selectedProperties = val.selectedProperties;
        }
        else if (val instanceof SecretStaticProperty) {
            clone = new SecretStaticProperty(id);
            clone.value = val.value;
            clone.isEncrypted = val.isEncrypted;
        }
        else if (val instanceof ColorPickerStaticProperty) {
            clone = new ColorPickerStaticProperty();
            clone.id = id;
            clone.selectedProperty = val.selectedColor;
        }
        else if (val instanceof GroupStaticProperty) {
            clone = new GroupStaticProperty(id);
            clone.staticProperties = val.staticProperties.map(elem => this.clone(elem));
            clone.showLabel = val.showLabel;
            clone.horizontalRendering = val.horizontalRendering;
          }
        else if (val instanceof AlternativesStaticProperty) {
            clone = new AlternativesStaticProperty(id);
            clone.alternatives = val.alternatives.map(elem => this.clone(elem));
        }
        else if (val instanceof AlternativeStaticProperty) {
            clone = new AlternativeStaticProperty(id);
            clone.selected = val.selected;
            clone.staticProperty = this.clone(val.staticProperty);
        }
        else if (val instanceof CollectionStaticProperty) {
            clone = new CollectionStaticProperty(id);
            clone.staticPropertyTemplate = this.clone(val.staticPropertyTemplate);
            clone.members = val.members.map(elem => this.clone(elem));
            clone.memberType = val.memberType;
        }
        else if (val instanceof URI) {
            clone = new URI(id);
            clone.tmp = val.tmp
        }
        //SelectionStaticProperty
        else if (val instanceof RuntimeResolvableAnyStaticProperty ||  val instanceof RuntimeResolvableOneOfStaticProperty){
            val instanceof RuntimeResolvableAnyStaticProperty ? clone = new RuntimeResolvableAnyStaticProperty(id) :
              clone = new RuntimeResolvableOneOfStaticProperty(id);

            clone.dependsOn = val.dependsOn;
            clone.value = val.value;
            clone.requiredDomainProperty = val.requiredDomainProperty;
            clone.options = val.options.map(option => this.cloneOption(option));
            clone.horizontalRendering = val.horizontalRendering;
        }
        else if (val instanceof AnyStaticProperty || val instanceof OneOfStaticProperty){
            val instanceof AnyStaticProperty ? clone = new AnyStaticProperty(id) : clone = new OneOfStaticProperty(id);

            clone.value = val.value;
            clone.requiredDomainProperty = val.requiredDomainProperty;
            clone.options = val.options.map(option => this.cloneOption(option));
            clone.horizontalRendering = val.horizontalRendering;
        }
        clone = this.copyStaticPropertyProperty(val, clone);
        return clone;
    }

    private copyStaticPropertyProperty(src: StaticProperty, dst: StaticProperty): StaticProperty {
        dst.label = src.label;
        dst.elementName = src.elementName;
        dst.description = src.description;
        dst.internalName = src.internalName;
        dst.index = src.index;
        return dst;
    }

    private cloneOption(val: Option) {
        let clone = new Option();
        clone.id = 'urn:streampipes.org:spi::' + this.generateID(6);
        clone.elementName = val.elementName;
        clone.name = val.name;
        clone.internalName = val.internalName;
        clone.selected = val.selected;
        return clone;
    }

    private generateID(length): string {
        let chars = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
        var result = '';
        for (var i = length; i > 0; --i) result += chars[Math.round(Math.random() * (chars.length - 1))];
        return result;
    }


    public asFreeTextStaticProperty(val: StaticProperty): FreeTextStaticProperty {
        return <FreeTextStaticProperty> val;
    }

    public asColorPickerStaticProperty(val: StaticProperty): ColorPickerStaticProperty {
        return <ColorPickerStaticProperty> val;
    }

    public asSecretStaticProperty(val: StaticProperty): SecretStaticProperty {
        return <SecretStaticProperty> val;
    }

    public asCollectionProperty(val: StaticProperty): CollectionStaticProperty {
        return <CollectionStaticProperty> val;
    }
}