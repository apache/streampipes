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

import { AssetLink, AssetLocation, Isa95Type } from '../gen/streampipes-model';

export interface AssetLinkType {
    linkType: string;
    linkLabel: string;
    linkColor: string;
    linkIcon?: string;
    linkQueryHint?: string;
    navPaths: string[];
    navigationActive: boolean;
}

export interface LinkageData {
    //Data Model to extract AssetLinks from the UI
    name: string;
    id: string;
    type: string;
    selected?: boolean | null;
}

export interface Isa95TypeDesc {
    label: string;
    type: Isa95Type;
}

export interface AssetSiteDesc {
    _id: string;
    _rev?: string;
    appDocType: string;
    label: string;
    location?: AssetLocation;
    areas: string[];
}

export interface SpAssetTreeNode {
    assetId: string;
    assetName: string;
    assetLinks: AssetLink[];
    assets?: SpAssetTreeNode[];
    spAssetModelId: string;
    flattenPath: any[];
}
