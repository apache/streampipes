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

import { Component } from '@angular/core';
import { DialogRef } from '@streampipes/shared-ui';
import { ShapeService } from '../../services/shape.service';
import { HyperlinkConfig } from '../../model/selected-visualization-data.model';

@Component({
    selector: 'sp-add-link-dialog-component',
    templateUrl: 'add-link-dialog.component.html',
    styleUrls: ['./add-link-dialog.component.scss'],
})
export class AddLinkDialogComponent {
    linkLabel: string;
    linkHref: string;
    labelFontSize = 12;
    newWindow = false;

    constructor(
        private dialogRef: DialogRef<AddLinkDialogComponent>,
        private shapeService: ShapeService,
    ) {}

    cancel() {
        this.dialogRef.close();
    }

    add() {
        const hyperlinkConfig: HyperlinkConfig = {
            linkLabel: this.linkLabel,
            linkHref: this.linkHref,
            labelFontSize: this.labelFontSize,
            newWindow: this.newWindow,
        };

        const group = this.shapeService.makeNewHyperlinkGroup(hyperlinkConfig);
        this.dialogRef.close(group);
    }
}
