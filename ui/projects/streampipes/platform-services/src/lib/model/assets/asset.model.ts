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

export interface AssetLinkType {
    linkType: string;
    linkLabel: string;
    linkColor: string;
    linkIcon?: string;
    linkQueryHint?: string;
    navPaths: string[];
    navigationActive: boolean;
}

export interface AssetType {
    assetIcon: string;
    assetIconColor: string;
    assetTypeCategory: string;
    assetTypeLabel: string;
}

export interface AssetLink {
    resourceId: string;
    linkType: 'data-view' | 'dashboard' | 'adapter' | 'source' | string;
    linkLabel: string;
    queryHint: string;
    editingDisabled: boolean;
    navigationActive: boolean;
}

export interface SpAsset {
    assetId: string;
    assetName: string;
    assetDescription: string;

    assetType: AssetType;
    assetLinks: AssetLink[];

    assets: SpAsset[];
}

export interface SpAssetModel extends SpAsset {
    _id: string;
    _rev: string;

    appDocType: string;

    removable: boolean;
}
