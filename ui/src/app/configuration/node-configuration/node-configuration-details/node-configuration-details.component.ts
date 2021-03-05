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

import {Component, Input, OnInit} from '@angular/core';
import {Message, NodeInfoDescription, PipelineOperationStatus} from "../../../core-model/gen/streampipes-model";
import {FormGroup} from "@angular/forms";
import {DialogRef} from "../../../core-ui/dialog/base-dialog/dialog-ref";
import {MatChipInputEvent} from "@angular/material/chips";
import {COMMA, ENTER} from "@angular/cdk/keycodes";
import {NodeService} from "../../../platform-services/apis/node.service";
import {PipelineStatusDialogComponent} from "../../../pipelines/dialog/pipeline-status/pipeline-status-dialog.component";
import {PanelType} from "../../../core-ui/dialog/base-dialog/base-dialog.model";
import {DialogService} from "../../../core-ui/dialog/base-dialog/base-dialog.service";
import {MatSnackBar} from "@angular/material/snack-bar";

export interface Fruit {
  name: string;
}

@Component({
  selector: 'node-configuration-details',
  templateUrl: './node-configuration-details.component.html',
  styleUrls: ['./node-configuration-details.component.scss']
})
export class NodeConfigurationDetailsComponent implements OnInit {

  submitNodeForm: FormGroup = new FormGroup({});
  saving: boolean = false;
  saved: boolean = false;
  advancedSettings: boolean;
  errorMessage: string = '';
  visible = true;
  selectable = true;
  removable = true;
  addOnBlur = true;
  readonly separatorKeysCodes: number[] = [ENTER, COMMA];
  tmpTags: string[];

  @Input()
  node: NodeInfoDescription;

  constructor(private nodeService: NodeService,
              private dialogRef: DialogRef<NodeConfigurationDetailsComponent>,
              private _snackBar: MatSnackBar) {
  }

  ngOnInit(): void {
    this.advancedSettings = false;
    this.tmpTags = this.node.staticNodeMetadata.locationTags.map(x => x);
  }

  updateNodeInfo() {
    let updateRequest;
    this.node.staticNodeMetadata.locationTags = this.tmpTags;
    updateRequest = this.nodeService.updateNodeState(this.node);

    updateRequest
        .subscribe(statusMessage => {
          if (statusMessage.success) {
            this.openSnackBar("Node successfully updated");
            this.hide();
          } else {
            this.openSnackBar("Node not updated")
          }
        });
  }

  hide() {
    this.dialogRef.close();
  }

  openSnackBar(message: string) {
    this._snackBar.open(message, "close", {
      duration: 3000,
    });
  }


  add(event: MatChipInputEvent): void {
    const input = event.input;
    const value = event.value;

    if ((value || '').trim()) {
      this.tmpTags.push(value.trim());
    }

    // Reset the input value
    if (input) {
      input.value = '';
    }
  }

  remove(tag: string): void {
    const index = this.tmpTags.indexOf(tag);

    if (index >= 0) {
      this.tmpTags.splice(index, 1);
    }
  }

}
