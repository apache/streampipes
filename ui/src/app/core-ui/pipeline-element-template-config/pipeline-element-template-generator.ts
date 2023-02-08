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
    SecretStaticProperty,
    StaticPropertyAlternative,
    StaticPropertyAlternatives,
    StaticPropertyGroup,
    StaticPropertyUnion,
    SlideToggleStaticProperty,
} from '@streampipes/platform-services';

export class PipelineElementTemplateGenerator {
    constructor(private sp: StaticPropertyUnion) {}

    public toTemplateValue(): any {
        if (this.sp instanceof FreeTextStaticProperty) {
            return this.sp.value;
        } else if (this.sp instanceof OneOfStaticProperty) {
            return this.sp.options.find(o => o.selected)
                ? this.sp.options.find(o => o.selected).name
                : '';
        } else if (this.sp instanceof ColorPickerStaticProperty) {
            return this.sp.selectedColor;
        } else if (this.sp instanceof SecretStaticProperty) {
            return { encrypted: this.sp.encrypted, value: this.sp.value };
        } else if (this.sp instanceof AnyStaticProperty) {
            return this.sp.options.filter(o => o.selected).map(o => o.name);
        } else if (this.sp instanceof CodeInputStaticProperty) {
            return this.sp.value;
        } else if (this.sp instanceof SlideToggleStaticProperty) {
            return this.sp.selected;
        } else if (this.sp instanceof CollectionStaticProperty) {
            return {
                members: this.addListEntry(this.sp.members),
            };
        } else if (this.sp instanceof FileStaticProperty) {
            return this.sp.locationPath;
        } else if (this.sp instanceof StaticPropertyAlternatives) {
            return {
                alternatives: this.addNestedEntry(this.sp.alternatives),
            };
        } else if (this.sp instanceof StaticPropertyAlternative) {
            const sp = this.sp.staticProperty
                ? this.addNestedEntry([this.sp.staticProperty])
                : {};
            return {
                selected: this.sp.selected,
                staticProperty: sp,
            };
        } else if (this.sp instanceof StaticPropertyGroup) {
            return {
                staticProperties: this.addNestedEntry(this.sp.staticProperties),
            };
        }
    }

    addEntry(sp: StaticPropertyUnion) {
        const entry = {};
        entry[sp.internalName] = new PipelineElementTemplateGenerator(
            sp,
        ).toTemplateValue();
        return entry;
    }

    addListEntry(staticProperties: StaticPropertyUnion[]) {
        const values = [];
        staticProperties.forEach(sp => {
            values.push(this.addEntry(sp));
        });
        return values;
    }

    addNestedEntry(staticProperties: StaticPropertyUnion[]) {
        const entry = {};
        staticProperties.forEach(sp => {
            entry[sp.internalName] = new PipelineElementTemplateGenerator(
                sp,
            ).toTemplateValue();
        });
        return entry;
    }
}
