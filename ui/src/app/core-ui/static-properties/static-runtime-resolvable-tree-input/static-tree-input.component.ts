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

import { Component, OnInit } from '@angular/core';
import { BaseRuntimeResolvableInput } from '../static-runtime-resolvable-input/base-runtime-resolvable-input';
import {
  RuntimeResolvableAnyStaticProperty,
  RuntimeResolvableTreeInputStaticProperty, StaticPropertyUnion
} from '../../../core-model/gen/streampipes-model';
import { RuntimeResolvableService } from '../static-runtime-resolvable-input/runtime-resolvable.service';

@Component({
  selector: 'sp-runtime-resolvable-tree-input',
  templateUrl: './static-tree-input.component.html',
  styleUrls: ['./static-tree-input.component.scss']
})
export class StaticRuntimeResolvableTreeInputComponent extends BaseRuntimeResolvableInput<RuntimeResolvableTreeInputStaticProperty> implements OnInit {

  constructor(runtimeResolvableService: RuntimeResolvableService) {
    super(runtimeResolvableService);
  }

  ngOnInit(): void {
    if (this.staticProperty.nodes.length === 0 && (!this.staticProperty.dependsOn || this.staticProperty.dependsOn.length === 0)) {
      this.loadOptionsFromRestApi();
    } else if (this.staticProperty.nodes.length > 0) {
      this.showOptions = true;
    }
    super.onInit();
  }

  parse(staticProperty: StaticPropertyUnion): RuntimeResolvableTreeInputStaticProperty {
    return staticProperty as RuntimeResolvableTreeInputStaticProperty;
  }

  afterOptionsLoaded(staticProperty: RuntimeResolvableTreeInputStaticProperty) {
    this.staticProperty = staticProperty;
    console.log(staticProperty);
  }

}
