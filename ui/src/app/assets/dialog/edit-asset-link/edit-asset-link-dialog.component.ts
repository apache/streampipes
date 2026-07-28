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

import { Component, Input, OnInit, inject } from '@angular/core';
import {
    DialogRef,
    FormFieldComponent,
    SearchSelectComponent,
} from '@streampipes/shared-ui';
import { AssetLink, AssetLinkType } from '@streampipes/platform-services';
import { FormsModule, UntypedFormGroup } from '@angular/forms';
import {
    MatOption,
    MatSelect,
    MatSelectChange,
} from '@angular/material/select';
import { BaseAssetLinksDirective } from '../base-asset-links.directive';
import { FlexDirective, LayoutDirective } from '@ngbracket/ngx-layout/flex';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatDivider } from '@angular/material/divider';
import { MatButton } from '@angular/material/button';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'sp-edit-asset-link-dialog-component',
    templateUrl: './edit-asset-link-dialog.component.html',
    imports: [
        FlexDirective,
        LayoutDirective,
        FormFieldComponent,
        MatFormField,
        MatSelect,
        MatOption,
        FormsModule,
        MatInput,
        MatDivider,
        MatButton,
        TranslatePipe,
        SearchSelectComponent,
    ],
})
export class EditAssetLinkDialogComponent
    extends BaseAssetLinksDirective
    implements OnInit
{
    private dialogRef =
        inject<DialogRef<EditAssetLinkDialogComponent>>(DialogRef);
    private translateService = inject(TranslateService);

    @Input()
    assetLink: AssetLink;

    @Input()
    assetLinkTypes: AssetLinkType[];

    @Input()
    createMode: boolean;

    parentForm: UntypedFormGroup;

    clonedAssetLink: AssetLink;

    currentResource: any;

    selectedLinkType: AssetLinkType;

    get resourceSelectionLabel(): string | undefined {
        switch (this.selectedLinkType.linkQueryHint) {
            case 'pipeline':
                return this.translateService.instant('Pipelines');
            case 'data-source':
                return this.translateService.instant('Data Stream');
            case 'dashboard':
                return this.translateService.instant('Dashboard');
            case 'chart':
                return this.translateService.instant('Chart');
            case 'adapter':
                return this.translateService.instant('Adapter');
            case 'measurement':
                return this.translateService.instant('Dataset');
            case 'file':
                return this.translateService.instant('Files');
            default:
                return undefined;
        }
    }

    get selectableResources(): any[] {
        switch (this.selectedLinkType.linkQueryHint) {
            case 'pipeline':
                return this.pipelines ?? [];
            case 'data-source':
                return this.dataSources ?? [];
            case 'dashboard':
                return this.dashboards ?? [];
            case 'chart':
                return this.charts ?? [];
            case 'adapter':
                return this.adapters ?? [];
            case 'measurement':
                return this.dataLakeMeasures ?? [];
            case 'file':
                return this.files ?? [];
            default:
                return [];
        }
    }

    ngOnInit(): void {
        super.onInit();
        this.clonedAssetLink = { ...this.assetLink };
        this.selectedLinkType = this.getCurrAssetLinkType();
    }

    getCurrAssetLinkType(): AssetLinkType {
        return this.assetLinkTypes.find(
            a => a.linkType === this.clonedAssetLink.linkType,
        );
    }

    store() {
        this.assetLink = this.clonedAssetLink;
        this.dialogRef.close(this.assetLink);
    }

    cancel() {
        this.dialogRef.close();
    }

    onLinkTypeChanged(event: MatSelectChange): void {
        this.selectedLinkType = event.value;
        const linkType = this.assetLinkTypes.find(
            a => a.linkType === this.selectedLinkType.linkType,
        );
        this.clonedAssetLink.editingDisabled = false;
        this.clonedAssetLink.linkType = linkType.linkType;
        this.clonedAssetLink.queryHint = linkType.linkQueryHint;
        this.clonedAssetLink.navigationActive = linkType.navigationActive;
        this.clonedAssetLink.resourceId = '';
        this.clonedAssetLink.linkLabel = '';
        this.currentResource = undefined;
    }

    changeLabel(id: string, label: string, currentResource: any) {
        this.clonedAssetLink.resourceId = id;
        this.clonedAssetLink.linkLabel = label;
        this.currentResource = currentResource;
    }

    onResourceChanged(currentResource: any): void {
        if (!currentResource || Array.isArray(currentResource)) {
            this.clonedAssetLink.resourceId = '';
            this.clonedAssetLink.linkLabel = '';
            this.currentResource = undefined;
            return;
        }

        if (this.selectedLinkType.linkQueryHint === 'file') {
            this.changeLabel(
                currentResource.fileId,
                currentResource.filename,
                currentResource,
            );
        } else if (this.selectedLinkType.linkQueryHint === 'measurement') {
            this.changeLabel(
                currentResource.elementId,
                currentResource.measureName,
                currentResource,
            );
        } else {
            this.changeLabel(
                currentResource.elementId,
                currentResource.name,
                currentResource,
            );
        }
    }

    afterResourcesLoaded(): void {
        if (!this.createMode) {
            this.currentResource = this.allResources.find(
                r => r.elementId === this.clonedAssetLink.resourceId,
            );
        }
    }
}
