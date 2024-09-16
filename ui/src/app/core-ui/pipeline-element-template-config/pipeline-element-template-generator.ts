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
    AnyStaticProperty,
    CodeInputStaticProperty,
    CollectionStaticProperty,
    ColorPickerStaticProperty,
    FileStaticProperty,
    FreeTextStaticProperty,
    OneOfStaticProperty,
    RuntimeResolvableTreeInputStaticProperty,
    SecretStaticProperty,
    SlideToggleStaticProperty,
    StaticPropertyAlternatives,
    StaticPropertyGroup,
    StaticPropertyUnion,
} from '@streampipes/platform-services';

export class PipelineElementTemplateGenerator {
    constructor(private sp: StaticPropertyUnion) {}

    public toTemplateValue(): Map<string, any> {
        const map = new Map();
        if (this.sp instanceof FreeTextStaticProperty) {
            map.set(this.sp.internalName, this.sp.value);
        } else if (this.sp instanceof OneOfStaticProperty) {
            const selectedOption = this.sp.options.find(o => o.selected);
            if (selectedOption !== undefined) {
                map.set(this.sp.internalName, selectedOption.name);
            }
        } else if (this.sp instanceof ColorPickerStaticProperty) {
            map.set(this.sp.internalName, this.sp.selectedColor);
        } else if (this.sp instanceof SecretStaticProperty) {
            map.set(this.sp.internalName, this.sp.value);
            map.set('encrypted', this.sp.encrypted);
        } else if (this.sp instanceof AnyStaticProperty) {
            map.set(
                this.sp.internalName,
                this.sp.options.filter(o => o.selected).map(o => o.name),
            );
        } else if (this.sp instanceof CodeInputStaticProperty) {
            map.set(this.sp.internalName, this.sp.value);
        } else if (this.sp instanceof SlideToggleStaticProperty) {
            map.set(this.sp.internalName, this.sp.selected);
        } else if (this.sp instanceof CollectionStaticProperty) {
            map.set(this.sp.internalName, this.addListEntry(this.sp.members));
        } else if (this.sp instanceof FileStaticProperty) {
            map.set(this.sp.internalName, this.sp.locationPath);
        } else if (this.sp instanceof StaticPropertyAlternatives) {
            const selectedAlternative = this.sp.alternatives.find(
                a => a.selected,
            );
            if (selectedAlternative !== undefined) {
                map.set(this.sp.internalName, selectedAlternative.internalName);
                const alternative = new PipelineElementTemplateGenerator(
                    selectedAlternative.staticProperty,
                ).toTemplateValue();
                alternative.forEach((value, key) => {
                    map.set(key, value);
                });
            }
        } else if (this.sp instanceof StaticPropertyGroup) {
            return this.addNestedEntry(this.sp.staticProperties);
        } else if (
            this.sp instanceof RuntimeResolvableTreeInputStaticProperty
        ) {
            map.set(this.sp.internalName, this.sp.selectedNodesInternalNames);
        }
        return map;
    }

    addListEntry(staticProperties: StaticPropertyUnion[]) {
        const values = [];
        staticProperties.forEach(sp => {
            values.push(
                new PipelineElementTemplateGenerator(sp).toTemplateValue(),
            );
        });
        return values;
    }

    addNestedEntry(staticProperties: StaticPropertyUnion[]) {
        const entry = new Map<string, any>();
        staticProperties.forEach(sp => {
            const groupEntries = new PipelineElementTemplateGenerator(
                sp,
            ).toTemplateValue();
            groupEntries.forEach((value, key) => {
                entry.set(key, value);
            });
        });
        return entry;
    }
}
