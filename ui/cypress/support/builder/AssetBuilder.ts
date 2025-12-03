/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import { Asset } from '../model/Asset';
import { Isa95Type } from '../../../projects/streampipes/platform-services/src/lib/model/gen/streampipes-model';

export class AssetBuilder {
    asset: Asset;

    constructor() {
        this.asset = new Asset();
        this.asset.labels = [];
        this.asset.subAssets = [];
        this.asset.assetType = 'OTHER';
    }

    public static create(name: string) {
        const builder = new AssetBuilder();
        builder.setName(name);
        return builder;
    }

    public setName(name: string) {
        this.asset.name = name;
        return this;
    }

    public setAssetType(type: Isa95Type) {
        this.asset.assetType = type;
        return this;
    }

    public addLabel(label: string) {
        if (!this.asset.labels) {
            this.asset.labels = [];
        }
        this.asset.labels.push(label);
        return this;
    }

    public setLabels(labels: string[]) {
        this.asset.labels = labels || [];
        return this;
    }

    public setSite(site: string) {
        this.asset.site = site;
        return this;
    }

    public addSubAsset(subAsset: Asset) {
        if (!this.asset.subAssets) {
            this.asset.subAssets = [];
        }
        this.asset.subAssets.push(subAsset);
        return this;
    }

    public addSubAssetBuilder(builder: AssetBuilder) {
        this.addSubAsset(builder.build());
        return this;
    }

    public build() {
        return this.asset;
    }
}
