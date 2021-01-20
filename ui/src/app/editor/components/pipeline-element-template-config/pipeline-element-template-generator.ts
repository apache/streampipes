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
  ColorPickerStaticProperty,
  FreeTextStaticProperty, OneOfStaticProperty, SecretStaticProperty,
  StaticPropertyUnion
} from "../../../core-model/gen/streampipes-model";

export class PipelineElementTemplateGenerator {

  constructor(private sp: StaticPropertyUnion) {

  }

  public toTemplateValue(): any {
    if (this.sp instanceof FreeTextStaticProperty) {
      return this.sp.value;
    } else if (this.sp instanceof OneOfStaticProperty) {
      return this.sp.options.find(o => o.selected).name;
    } else if (this.sp instanceof ColorPickerStaticProperty) {
      return this.sp.selectedColor;
    } else if (this.sp instanceof SecretStaticProperty) {
      return undefined;
    } else if (this.sp instanceof AnyStaticProperty) {
      return this.sp.options.filter(o => o.selected).map(o => o.name);
    }
  }
}
