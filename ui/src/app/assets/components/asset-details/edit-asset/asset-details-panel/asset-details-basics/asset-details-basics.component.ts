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
    Component,
    EventEmitter,
    Input,
    OnChanges,
    OnInit,
    Output,
    SimpleChanges,
    inject,
} from '@angular/core';
import {
    AssetSiteDesc,
    Isa95TypeDesc,
    Isa95TypeService,
    SpAsset,
    SpAssetModel,
} from '@streampipes/platform-services';
import { FlexDirective, LayoutDirective } from '@ngbracket/ngx-layout/flex';
import {
    FormFieldComponent,
    SplitSectionComponent,
} from '@streampipes/shared-ui';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { MatOption, MatSelect } from '@angular/material/select';
import { AssetDetailsLabelsComponent } from './asset-details-labels/asset-details-labels.component';
import { AssetDetailsCustomFieldsComponent } from './asset-details-custom-fields/asset-details-custom-fields.component';
import { AssetDetailsSiteComponent } from './asset-details-site/asset-details-site.component';
import { AssetDetailsLinksComponent } from '../asset-details-links/asset-details-links.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-asset-details-basics',
    templateUrl: './asset-details-basics.component.html',
    imports: [
        LayoutDirective,
        SplitSectionComponent,
        FormFieldComponent,
        MatFormField,
        MatInput,
        FormsModule,
        FlexDirective,
        MatSelect,
        MatOption,
        AssetDetailsLabelsComponent,
        AssetDetailsCustomFieldsComponent,
        AssetDetailsSiteComponent,
        AssetDetailsLinksComponent,
        TranslatePipe,
    ],
})
export class AssetDetailsBasicsComponent implements OnInit, OnChanges {
    private isa95TypeService = inject(Isa95TypeService);

    @Input()
    asset: SpAsset;

    @Input()
    assetModel: SpAssetModel;

    @Input()
    editMode: boolean;

    @Input()
    isNewAsset: boolean;

    @Input()
    rootNode: boolean;

    @Input()
    sites: AssetSiteDesc[];

    @Output()
    reloadSites: EventEmitter<void> = new EventEmitter();

    isa95Types: Isa95TypeDesc[] = [];

    ngOnInit() {
        this.isa95Types = this.isa95TypeService.getTypeDescriptions();
    }

    ngOnChanges(changes: SimpleChanges) {
        if (changes['asset']) {
            this.asset.assetType ??= {
                assetIcon: undefined,
                assetIconColor: undefined,
                assetTypeCategory: undefined,
                assetTypeLabel: undefined,
                isa95AssetType: 'OTHER',
            };
        }
    }
}
