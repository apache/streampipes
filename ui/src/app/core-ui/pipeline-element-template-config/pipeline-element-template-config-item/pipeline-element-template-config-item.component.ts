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
import { StaticPropertyUnion } from '@streampipes/platform-services';
import { PipelineElementTemplateGenerator } from '../pipeline-element-template-generator';

@Component({
    selector: 'sp-pipeline-element-template-config-item',
    templateUrl: './pipeline-element-template-config-item.component.html',
})
export class PipelineElementTemplateConfigItemComponent implements OnInit {
    @Input()
    templateConfigs: Map<string, any>[] = [];

    @Input()
    sp: StaticPropertyUnion;

    selected = true;

    ngOnInit(): void {
        this.calculateSelected();
    }

    calculateSelected(): void {
        this.selected =
            this.templateConfigs.find(config =>
                config.has(this.sp.internalName),
            ) !== undefined;
    }

    handleSelection() {
        if (this.selected) {
            this.templateConfigs = this.templateConfigs.filter(
                c => !c.has(this.sp.internalName),
            );
        } else {
            this.templateConfigs.push(
                new PipelineElementTemplateGenerator(this.sp).toTemplateValue(),
            );
        }
        this.calculateSelected();
    }
}
