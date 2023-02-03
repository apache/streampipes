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
import {
    DataProcessorInvocation,
    OutputStrategy,
} from '@streampipes/platform-services';
import { UntypedFormGroup } from '@angular/forms';

@Component({
    selector: 'sp-output-strategy',
    templateUrl: './output-strategy.component.html',
    styleUrls: ['./output-strategy.component.scss'],
})
export class OutputStrategyComponent implements OnInit {
    @Input()
    parentForm: UntypedFormGroup;

    @Input()
    outputStrategy: OutputStrategy;

    @Input()
    selectedElement: DataProcessorInvocation;

    @Input()
    restrictedEditMode: boolean;

    label: string;

    customizableOutputStrategy: boolean;

    ngOnInit(): void {
        this.customizableOutputStrategy =
            this.outputStrategy['@class'] ===
                'org.apache.streampipes.model.output.CustomOutputStrategy' ||
            this.outputStrategy['@class'] ===
                'org.apache.streampipes.model.output.UserDefinedOutputStrategy';
    }
}
