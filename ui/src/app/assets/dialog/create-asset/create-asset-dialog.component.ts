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

import { Component, Input, inject } from '@angular/core';
import {
    AssetManagementService,
    SpAssetModel,
} from '@streampipes/platform-services';
import { DialogRef } from '@streampipes/shared-ui';
import {
    FlexDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { FormFieldComponent } from '@streampipes/shared-ui';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { MatDivider } from '@angular/material/divider';
import { MatButton } from '@angular/material/button';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-create-asset-dialog-component',
    templateUrl: './create-asset-dialog.component.html',
    imports: [
        FlexDirective,
        LayoutDirective,
        FormFieldComponent,
        MatFormField,
        MatInput,
        FormsModule,
        MatDivider,
        LayoutGapDirective,
        MatButton,
        TranslatePipe,
    ],
})
export class SpCreateAssetDialogComponent {
    private dialogRef =
        inject<DialogRef<SpCreateAssetDialogComponent>>(DialogRef);
    private assetManagementService = inject(AssetManagementService);

    @Input() assetModel: SpAssetModel;

    onCancel(): void {
        this.dialogRef.close();
    }

    onSave(): void {
        this.assetManagementService
            .createAsset(this.assetModel)
            .subscribe(() => {
                this.dialogRef.close(true);
            });
    }
}
