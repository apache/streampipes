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
import { SpAsset } from '@streampipes/platform-services';

export interface CustomField {
    key: string;
    value: string;
}

@Component({
    selector: 'sp-asset-details-custom-fields',
    templateUrl: './asset-details-custom-fields.component.html',
    standalone: false,
})
export class AssetDetailsCustomFieldsComponent implements OnInit {
    @Input()
    asset: SpAsset;

    @Input()
    editMode = false;

    ngOnInit() {
        this.asset.additionalData.customFields ??= [];
    }

    get customFields(): CustomField[] {
        return this.asset.additionalData!.customFields!;
    }

    addCustomField(): void {
        this.customFields.push({ key: '', value: '' });
    }

    removeCustomField(index: number): void {
        this.customFields.splice(index, 1);
    }
}
