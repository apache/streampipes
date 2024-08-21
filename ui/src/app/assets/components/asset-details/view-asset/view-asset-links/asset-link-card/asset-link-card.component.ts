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
import { AssetLink, AssetLinkType } from '@streampipes/platform-services';
import { Router } from '@angular/router';

@Component({
    selector: 'sp-asset-link-card',
    templateUrl: './asset-link-card.component.html',
    styleUrls: ['./asset-link-card.component.scss'],
})
export class AssetLinkCardComponent implements OnInit {
    @Input()
    assetLink: AssetLink;

    @Input()
    assetLinkTypes: AssetLinkType[] = [];

    linkType: AssetLinkType;

    boxStyle: any;
    linkStyle: any;

    constructor(private router: Router) {}

    ngOnInit() {
        this.linkType = this.assetLinkTypes.find(
            l => l.linkType === this.assetLink.linkType,
        );
        this.boxStyle = { border: `1px solid ${this.linkType.linkColor}` };
        this.linkStyle = { color: this.linkType.linkColor };
    }

    navigate() {
        this.router.navigate([
            ...this.linkType.navPaths,
            this.assetLink.resourceId,
        ]);
    }
}
