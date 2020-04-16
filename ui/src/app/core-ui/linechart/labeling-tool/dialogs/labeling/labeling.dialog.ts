/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Component, EventEmitter, Inject, Input, OnInit, Output } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

export interface IDialogData {
    startX: string;
    endX: string;
    n_selected_points: number;
    labels: object;
    selected_label: string;
}

@Component({
    selector: 'sp-dialog-labeling',
    templateUrl: './labeling.dialog.html',
    styleUrls: ['./labeling.dialog.css']
})
export class LabelingDialog implements OnInit {

    constructor(
        public dialogRef: MatDialogRef<LabelingDialog>,
        @Inject(MAT_DIALOG_DATA) public data: IDialogData) {
    }

    ngOnInit(): void {
    }

    onNoClick(): void {
        this.dialogRef.close();
    }

    handleSelectedLabelChange(label: {category, name}) {
        this.data.selected_label = label.name;
    }

}
