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

import { EventSchema, StaticProperty, StaticPropertyUnion } from '@streampipes/platform-services';
import { Directive, EventEmitter, Input, Output } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ConfigurationInfo } from '../../../connect/model/ConfigurationInfo';
import { InvocablePipelineElementUnion } from '../../../editor/model/editor.model';

@Directive()
// eslint-disable-next-line @angular-eslint/directive-class-suffix
export abstract class AbstractStaticPropertyRenderer<T extends StaticProperty> {

  @Input()
  staticProperty: T;

  @Input()
  staticProperties: StaticPropertyUnion[];

  @Input()
  eventSchemas: EventSchema[];

  @Input()
  adapterId: string;

  @Input()
  pipelineElement: InvocablePipelineElementUnion;

  @Input()
  parentForm: UntypedFormGroup;

  @Input()
  fieldName: string;

  @Input()
  displayRecommended: boolean;

  @Output() updateEmitter: EventEmitter<ConfigurationInfo> = new EventEmitter();


  constructor() {

  }

  emitUpdate(valid?: boolean) {
    this.updateEmitter.emit(new ConfigurationInfo(this.staticProperty.internalName, valid));
  }

}
