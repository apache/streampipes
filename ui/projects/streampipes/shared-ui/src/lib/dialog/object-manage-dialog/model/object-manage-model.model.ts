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

import { Permission, SpAssetTreeNode } from '@streampipes/platform-services';
import { Observable } from 'rxjs';

export type ObjectManageDialogSaveMode = 'deferred' | 'immediate';

export interface ObjectManageDialogResource {
    _id?: string;
    elementId?: string;
    title?: string;
    name?: string;
    description?: string;
    updatedAt?: number;
    lastModified?: number;
}

export interface ObjectManageDialogResourceConfig<
    TResource extends ObjectManageDialogResource = ObjectManageDialogResource,
> {
    resourceLabel?: string;
    nameLabel?: string;
    descriptionLabel?: string;
    idProperty?: '_id' | 'elementId';
    nameProperty?: 'title' | 'name';
    descriptionProperty?: string;
    showResourceFields?: boolean;
    showAssetLinking?: boolean;
    assetLinkType?: string;
    assetLinkCheckboxLabel?: string;
    saveResource?: (
        resource: TResource,
    ) => Observable<unknown> | Promise<unknown>;
}

export interface ObjectManageDialogResult<
    TResource extends ObjectManageDialogResource = ObjectManageDialogResource,
> {
    resource: TResource;
    nb?: TResource;
    permission?: Permission;
    selectedAssets: SpAssetTreeNode[];
    deselectedAssets: SpAssetTreeNode[];
    originalAssets: SpAssetTreeNode[];
    addToAssets: boolean;
}
